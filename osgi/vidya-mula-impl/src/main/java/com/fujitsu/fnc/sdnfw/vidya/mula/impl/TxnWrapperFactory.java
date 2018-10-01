package com.fujitsu.fnc.sdnfw.vidya.mula.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.fujitsu.fnc.sdnfw.vidya.mula.api.Edge;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.Node;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.VersionNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class TxnWrapperFactory {
	
	private final ListeningExecutorService txn_Actor_Pool;
	private final int actorsCount = 10;
	private static TxnWrapperFactory instance = null;
	
	Map<String,Object> nodes_map = new HashMap<>();
	ReadWriteLock txnLock = new ReentrantReadWriteLock();
	
	public TxnWrapperFactory() {
		txn_Actor_Pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(actorsCount));
	}
	
	public void startUp() {
		System.out.println("Transaction Wrapper Bean Startup");
	}

	public synchronized static TxnWrapperFactory getInstance() 
	{
		synchronized(TxnWrapperFactory.class) {
			if(instance == null)
				instance = new TxnWrapperFactory();
		}
		
		return instance;
	}
	

	/*
	 * Asynchronous Update
	 * 
	 * Atomic Update of nodes in the tree
	 * Two ways
	 * 1. Traverse through the tree and get the node objects and then update the node
	 * 2. Maintain a cache of all nodes with respective nodeID as key. Get the node from cache(if present) and update.
	 * 
	 * @Input : List of nodes with the changed(or)new properties
	 * Asynchronous update of nodes properties
	 * Throws Exception - Customized Exception can be thrown.
	 */ 
	
	public synchronized void updateNodes(Node topNode, Map<String,Map<String,Object>> nodeId_AttrsMap)
			throws Exception
	{
		Set<String> node_Ids = nodeId_AttrsMap.keySet();
		
		//Read nodes for all the target nodes to be updated
		List<Node> read_nodes = (List<Node>) readNodes(topNode,node_Ids);
		
		//Lock for this set of updates, until the transactions are committed.
		txnLock.writeLock().lock();
	
		try {
			read_nodes.forEach( targetNode -> {
			
				assert(targetNode != null); //node not null 
				Map<String, Object> nodeAttrs = nodeId_AttrsMap.get(targetNode.getId());
				
				WriteTxnService txnService = new WriteTxnService(targetNode,nodeAttrs);
				txn_Actor_Pool.submit(txnService);
			
			});
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			//Release the write lock
			txnLock.writeLock().unlock();
		}
	}
	/*
	 * Read the Nodes (Depth First traversal)
	 * For each ID in the collection
	 * 
	 * Return - Collection of Nodes for the given input node_ids
	 * Throws Exception - Customized Exception can be thrown.
	 */
	public Collection<Node> readNodes(Node topNode, Collection<String> node_ids) throws Exception
	{
		List<Node> nodes = new ArrayList<>();
		this.nodes_map = DataStoreMgrImpl.getInstance().getNodes_map();
		txnLock.readLock().lock();
		try {
			node_ids.forEach( id -> {
				try {
					Node rootNode = (Node)nodes_map.get("ONF");
					ReadTxnService txnService = new ReadTxnService(rootNode,id);
					ListenableFuture<Node> future = txn_Actor_Pool.submit(txnService);
					nodes.add(future.get());
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			});
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			txnLock.readLock().unlock();
		}

		return nodes;
	}
	
	/*
	 * Traverse through the tree and log the nodes that are modified with respect to the attributes
	 * Each Node contains collection of VersionNodes, which is incremented for each node update.
	 * This version node contains previous snapshot attributes and time stamp.
	 */
	List<String> traversedNodeIds = new ArrayList<>();
	Stack<Node> stack = new Stack<>();
	List<Node> auditResult = new ArrayList<>();
	
	public List<Node> getAuditResult() {
		return auditResult;
	}

	public void appendAuditResult(Node auditNode) {
		this.auditResult.add(auditNode);
	}

	public List<Node> auditTree() throws Exception 
	{
		try {
			//Clear the traversed Node Ids and Stack
			auditResult = new ArrayList<>();
			traversedNodeIds = new ArrayList<>();
			stack = new Stack<>();
			
			Node rootNode = (Node)nodes_map.get("ONF");
			traverseTree(rootNode);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return auditResult;
	}
	
	/*
	 * Traverse the tree Depth First
	 */
	private void traverseTree(Node topNode)
	{
		stack.push(topNode);
		while(!stack.empty()) {
			Node node = stack.pop();
			List<VersionNode> versionNodes = node.getVersion_Nodes();
			if(versionNodes.size() != 0)
				this.appendAuditResult(node);
						
			for(Edge edge : node.getEdges()) {
				Node childNode = edge.getToNode();
					
				if(!traversedNodeIds.contains(childNode.getId())) 
				{
					traversedNodeIds.add(childNode.getId());
//					System.out.println(childNode.getId());
					traverseTree(childNode);
				}
			}
		}
	}
}
