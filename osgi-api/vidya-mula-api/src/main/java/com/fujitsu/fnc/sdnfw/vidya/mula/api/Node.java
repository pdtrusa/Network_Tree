package com.fujitsu.fnc.sdnfw.vidya.mula.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	protected String type;
	protected String id;
	
	protected Map<String,Object> attrs = new HashMap<>();
	protected List<Edge> edges = new ArrayList<>();
	List<VersionNode> version_Nodes = new ArrayList<>();
	
	public Node(String type, String id) {
		this.type = type;
		this.id = id;
		this.attrs.clear();
	}
	
	public Node(String type, String id, Map<String,Object> attrs) {
		this.type = type;
		this.id = id;
		this.attrs.putAll(attrs);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, Object> attrs) {
		this.attrs = attrs;
	}
	
	public void setAttrs(Map<String, Object> attrs,boolean replaceAll) {
		if(replaceAll)
			this.attrs.clear();
		this.attrs.putAll(attrs);
	}
	
	public void updateAttr(String key, Object val) {
		this.attrs.put(key, val);
	}
	
	public void updateAttrs(Map<String,Object> attrs) {
		attrs.forEach((k,v) -> this.attrs.put(k,v));
	}
	
	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	
	public void addEdge(Edge edge) {
		if(edge != null)
			this.edges.add(edge);
	}

	public List<VersionNode> getVersion_Nodes() {
		return version_Nodes;
	}

	public void setVersion_Nodes(List<VersionNode> previous_version_Nodes) {
		this.version_Nodes = previous_version_Nodes;
	}
	
	public void addVersionNode(VersionNode versionNode) {
		this.version_Nodes.add(versionNode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (attrs == null) {
			if (other.attrs != null)
				return false;
		} else if (!attrs.equals(other.attrs))
			return false;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	private String getVersions() {
		StringBuffer buff = new StringBuffer();
		
		List<VersionNode> versionNodes = getVersion_Nodes();
		for(int i=0; i<versionNodes.size();i++)
			buff.append("##Version:"+(i+1)+"##\n"+versionNodes.get(i)+"\n------\n");
			
		
		return buff.toString();
	}
	@Override
	public String toString() {
		return "Node [type=" + type + ", id=" + id + ", attrs=" + attrs
				+ ", edges=" + edges +"\n"+ getVersions(); 
//				"]";
	}
}
