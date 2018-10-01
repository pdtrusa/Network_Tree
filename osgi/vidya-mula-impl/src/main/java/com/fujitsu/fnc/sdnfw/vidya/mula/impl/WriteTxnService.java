package com.fujitsu.fnc.sdnfw.vidya.mula.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.fujitsu.fnc.sdnfw.vidya.mula.api.Node;
import com.fujitsu.fnc.sdnfw.vidya.mula.api.VersionNode;

public class WriteTxnService implements Callable<Boolean> {
	
	private Node target_node;
	private Map<String,Object> nodeAttrs = new HashMap<>();
	
	public WriteTxnService(Node node, Map<String,Object> attrs) {
		this.target_node = node;
		this.nodeAttrs = attrs;
	}
	
	@Override
	public Boolean call() throws Exception 
	{
		try {
			synchronized(target_node) { //Already Attributes Map is synchronized at Node object level
				
				//Clone the Node object and copy the attributes to temporary Node
				Node tempNode = new Node(target_node.getType(), target_node.getId());
				tempNode.setAttrs(target_node.getAttrs(),true);
				VersionNode versionNode = new VersionNode(tempNode);
				
//				System.out.println("Before Update:"+target_node);
				
				target_node.addVersionNode(versionNode);//Add previous versions
				target_node.updateAttrs(this.nodeAttrs);
				
//				System.out.println("After Update:"+target_node);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
			
		return true; 
	}
}
