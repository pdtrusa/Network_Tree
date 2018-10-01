package com.fujitsu.fnc.sdnfw.vidya.mula.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DataStoreService {
	public void myService();
	public void constructNetworkTree();
	public List<String> execute_AsyncRequest(String request, List<String> inputs);
	public List<String> execute_SyncRequest(String request, List<String> inputs);
	
	public Collection<Node> readNodes(Node topNode, Collection<String> node_ids) throws Exception;
	public void updateNodes(Node topNode, Map<String,Map<String,Object>> nodeId_AttrsMap) throws Exception;
	public List<Node> auditTree() throws Exception;
	
}
