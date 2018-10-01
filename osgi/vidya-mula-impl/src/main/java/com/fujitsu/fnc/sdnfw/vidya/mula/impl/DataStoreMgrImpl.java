package com.fujitsu.fnc.sdnfw.vidya.mula.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.fujitsu.fnc.sdnfw.vidya.mula.api.Constants;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.Edge;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.Node;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.DataStoreService;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.VersionNode;

public class DataStoreMgrImpl implements DataStoreService { 
	
	private static DataStoreMgrImpl instance = null;
	
	Map<String,Object> nodes_map = new HashMap<>();
	ReadWriteLock txnLock = new ReentrantReadWriteLock();
	TxnWrapperFactory txnWrapperFactory = null;
	
	public DataStoreMgrImpl() {
		txnWrapperFactory = TxnWrapperFactory.getInstance();
		
	}
	
	public void startUp() {
		System.out.println("Vidya Mula Bean Startup");
	}

	public synchronized static DataStoreMgrImpl getInstance() 
	{
		synchronized(DataStoreMgrImpl.class) {
			if(instance == null)
				instance = new DataStoreMgrImpl();
		}
		
		return instance;
	}
	
	@Override
	public void myService() {
		System.out.println("Vidya Mula OSGI Service");
		
	}
	
	public Map<String, Object> getNodes_map() {
		return nodes_map;
	}

	public void setNodes_map(Map<String, Object> nodes_map) {
		this.nodes_map = nodes_map;
	}

	@Override
	/*
	 * Build the tree with some sample data with hard coded values
	 * Values can be read from any other input source.
	 */
	
	public void constructNetworkTree() {
		HashMap<String,Object> rootNodeAttrs = new HashMap<>();
		rootNodeAttrs.put(Constants.NODE_LOCATION, "Menlo Park");
		rootNodeAttrs.put(Constants.NODE_LATITUDE, "0.0");
		
		Node orgNode = new Node("ONF","ONF");
		nodes_map.put("ONF", orgNode);
		
		String edgeName = "CONTAINS";
		List<Node> switchNodes = new ArrayList<>();
		for(int i=0; i<2; i++) {
			String id = "ONF:SWITCH-"+(i+1);
			HashMap<String,Object> nodeAttrs = new HashMap<>();
			nodeAttrs.put(Constants.NODE_LOCATION, "SanFrancisco");
			nodeAttrs.put(Constants.NODE_LATITUDE, "9.9");
					
			Node switchNode = new Node("SWITCH", id, nodeAttrs );
			switchNodes.add(switchNode);
			
			Map<String,Object> edgeProps = new HashMap<>();
			edgeProps.put(Constants.EDGE_WEIGHT, Integer.valueOf(1));
			edgeProps.put(Constants.EDGE_LATENCY,Integer.valueOf(1));
			
			
			Edge edge = new Edge(edgeName,orgNode,switchNode,edgeProps);
			orgNode.addEdge(edge);
			nodes_map.put(id, switchNode);
		}
		
		List<Node> physicalTPs = new ArrayList<>();
		switchNodes.forEach(switchNode -> {
			for(int i=0; i<3; i++) {
				String id = switchNode.getId()+":PTP-"+(i+1);
				HashMap<String,Object> nodeAttrs = new HashMap<>();
				nodeAttrs.put(Constants.NODE_LOCATION, "Sunnyvale");
				nodeAttrs.put(Constants.NODE_LATITUDE, "8.8");
				
				Node physicalTP = new Node("PHYSICAL_TERMINATION_PT",id,nodeAttrs);
				physicalTPs.add(physicalTP);
				
				Map<String,Object> edgeProps = new HashMap<>();
				edgeProps.put(Constants.EDGE_WEIGHT, Integer.valueOf(2));
				edgeProps.put(Constants.EDGE_LATENCY,Integer.valueOf(2));
				
				Edge edge = new Edge(edgeName,switchNode,physicalTP,edgeProps);
				switchNode.addEdge(edge);
				nodes_map.put(id, physicalTP);
			}
		});
		
		List<Node> logicalTPs = new ArrayList<>();
		physicalTPs.forEach(physicalTP -> {
			for(int i=0; i<3; i++) {
				String id = physicalTP.getId()+":LTP-"+(i+1);
				HashMap<String,Object> nodeAttrs = new HashMap<>();
				nodeAttrs.put(Constants.NODE_LOCATION, "San Jose");
				nodeAttrs.put(Constants.NODE_LATITUDE, "7.7");
				
				Node logicalTP = new Node("LOGICAL_TERMINATION_PT",id,nodeAttrs);
				logicalTPs.add(logicalTP);
				
				Map<String,Object> edgeProps = new HashMap<>();
				edgeProps.put(Constants.EDGE_WEIGHT, Integer.valueOf(3));
				edgeProps.put(Constants.EDGE_LATENCY,Integer.valueOf(3));
				
				Edge edge = new Edge(edgeName,physicalTP,logicalTP,edgeProps);
				physicalTP.addEdge(edge);
				nodes_map.put(id, logicalTP);
			}
		});
	}
	
	@Override
	public Collection<Node> readNodes(Node topNode, Collection<String> node_ids) throws Exception
	{
		return this.txnWrapperFactory.readNodes(topNode, node_ids);
	}

	@Override
	public void updateNodes(Node topNode, Map<String, Map<String, Object>> nodeId_AttrsMap) throws Exception 
	{
		this.txnWrapperFactory.updateNodes(topNode, nodeId_AttrsMap);
	}

	@Override
	public List<Node> auditTree() throws Exception
	{
		return this.txnWrapperFactory.auditTree()
	}
	
	
	
	/* ############# REST API CALLS for Testing
	 */
	
	public String printNetworkTree() {
		StringBuffer buff = new StringBuffer();
		Node rootNode = (Node)nodes_map.get("ONF");
		buff.append(rootNode.getId()+"\n");
		getChildren(rootNode, buff);
		return buff.toString();
	}
	
	private void getChildren(Node node, StringBuffer buff) {
		buff.append("####\t");
		node.getEdges().forEach(edge -> {
			Node toNode = edge.getToNode();
			buff.append(toNode.getId()+"\n");
			getChildren(toNode, buff);
		});
	}
	
	public Collection<Node> readNodes() {
		Collection<Node> nodes = null;
//		List<String> nodeIds = Arrays.asList(new String[]{"ONF:SWITCH-2:PTP-1","ONF:SWITCH-1:PTP-1:LTP-2","ONF:SWITCH-1:PTP-2"});
		List<String> nodeIds = Arrays.asList(new String[]{"ONF:SWITCH-1:PTP-1:LTP-2","ONF:SWITCH-1:PTP-2"});
		try {
			Thread.sleep(2000);
			nodes = TxnWrapperFactory.getInstance().readNodes(null, nodeIds);
			nodes.forEach(node -> {System.out.println(node);});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return nodes;
	}
	
	List<String> traversedNodeIds = new ArrayList<>();
	Stack<Node> stack = new Stack<>();
	
	public void traverseTree(Node topNode)
	{
		stack.push(topNode);
		while(!stack.empty()) {
			Node node = stack.pop();
						
			for(Edge edge : node.getEdges()) {
				Node childNode = edge.getToNode();
					
				if(!traversedNodeIds.contains(childNode.getId())) {
					traversedNodeIds.add(childNode.getId());
//					stack.push(childNode);
					System.out.println(childNode.getId());
					traverseTree(childNode);
				}
			}
		}
			
		return;
	}
	
	///////////////////////////////////////////
	///////////////////////////////////////////
	/*
	 * ######################################################
	 * TESTING with MAIN method
	 */
	
	public static void main(String[] args) {
		//BEGIN TREE Population
		
		DataStoreMgrImpl.getInstance().constructNetworkTree();
		System.out.println(DataStoreMgrImpl.getInstance().printNetworkTree());
		
		//END TREE Population
		
		//#########################
		
		//BEGIN READ
		//Read multiple nodes in the tree
		//Input : Node IDs
		
		DataStoreMgrImpl.getInstance().readNodes();
		
		//END of Read
		
		//#########################
		
		//BEGIN Atomic Multi node update
		Map<String,Map<String,Object>> updateNodesMap = new HashMap<>();
		Map<String,Object> nodeProps = new HashMap<>();
		nodeProps.put("node_location", "Asia");
		nodeProps.put("New_Attr", "New_Value");
		updateNodesMap.put("ONF:SWITCH-1:PTP-1:LTP-2", nodeProps);
		
		nodeProps = new HashMap<>();
		nodeProps.put("node_location", "Europe");
		nodeProps.put("New_Attr", "New_Value");
		updateNodesMap.put("ONF:SWITCH-1:PTP-2", nodeProps);
		
		try {
			TxnWrapperFactory.getInstance().updateNodes(null, updateNodesMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("#######################");
		
		//Read after update
//		DataStoreMgrImpl.getInstance().readNodes();
		
		//BEGIN Atomic Multi node update
		updateNodesMap = new HashMap<>();
		nodeProps = new HashMap<>();
		nodeProps.put("node_location", "Asia_2");
		nodeProps.put("New_Attr_2", "New_Value_2");
		updateNodesMap.put("ONF:SWITCH-1:PTP-1:LTP-2", nodeProps);
				
		nodeProps = new HashMap<>();
		nodeProps.put("node_location", "Europe_2");
		nodeProps.put("New_Attr_2", "New_Value_2");
		updateNodesMap.put("ONF:SWITCH-1:PTP-2", nodeProps);
		
/*		nodeProps = new HashMap<>();
		nodeProps.put("node_location", "Swiss");
		nodeProps.put("New_Attr", "New_Value");
		updateNodesMap.put("ONF:SWITCH-2:PTP-1", nodeProps);
*/				
		try {
			TxnWrapperFactory.getInstance().updateNodes(null, updateNodesMap);
			Thread.sleep(2000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
				
		System.out.println("#######################");
		DataStoreMgrImpl.getInstance().readNodes();
		
		//Audit entire snapshot
		List<Node> auditedResult;
		try {
			auditedResult = TxnWrapperFactory.getInstance().auditTree();
			StringBuffer buff = new StringBuffer();
			buff.append("\n\n $$$$$$ AUDIT RESULT $$$$$$$\n");
			for(Node node : auditedResult)
				buff.append("###########\n"+node+"\n");
			
			System.out.println(buff.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	/*
	 * @Unused.. For further enhancements. Can be used to update the functionality.
	 */
	@Override
	public List<String> execute_AsyncRequest(String request, List<String> inputs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> execute_SyncRequest(String request, List<String> inputs) {
		// TODO Auto-generated method stub
		return null;
	}



}
