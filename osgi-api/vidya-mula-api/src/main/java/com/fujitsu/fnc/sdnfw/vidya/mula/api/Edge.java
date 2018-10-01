package com.fujitsu.fnc.sdnfw.vidya.mula.api;

import java.util.HashMap;
import java.util.Map;

public class Edge {
	private String edgeName;
	private Node fromNode;
	private Node toNode;

	private Map<String,Object> properties = new HashMap<>();
	
	public Edge(String name, Node from_Node, Node to_Node) {
		this.edgeName = name;
		this.fromNode = from_Node;
		this.toNode = to_Node;
		this.properties = new HashMap<>();
	}
	
	public Edge(String name, Node from_Node, Node to_Node, Map<String,Object> props) {
		this.edgeName = name;
		this.fromNode = from_Node;
		this.toNode = to_Node;
		this.properties = props;
	}
	
	public String getEdgeName() {
		return edgeName;
	}

	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((edgeName == null) ? 0 : edgeName.hashCode());
		result = prime * result
				+ ((fromNode == null) ? 0 : fromNode.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((toNode == null) ? 0 : toNode.hashCode());
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
		Edge other = (Edge) obj;
		if (edgeName == null) {
			if (other.edgeName != null)
				return false;
		} else if (!edgeName.equals(other.edgeName))
			return false;
		if (fromNode == null) {
			if (other.fromNode != null)
				return false;
		} else if (!fromNode.equals(other.fromNode))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (toNode == null) {
			if (other.toNode != null)
				return false;
		} else if (!toNode.equals(other.toNode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [edgeName=" + edgeName + ", fromNode=" + fromNode.getId()
				+ ", toNode=" + toNode.getId()+ "]";
	}

	
}
