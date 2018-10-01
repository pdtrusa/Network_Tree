package com.fujitsu.fnc.sdnfw.vidya.mula.api;

import java.time.LocalDateTime;

public class VersionNode {
	LocalDateTime timeStamp;
	Node node_Snapshot = null;
	
	public VersionNode(Node node_Snapshot) {
		this.timeStamp = LocalDateTime.now();
		this.node_Snapshot = node_Snapshot;
	}
	
	public Node getNode_Snapshot() {
		return node_Snapshot;
	}
	
	public void setNode_Snapshot(Node node_Snapshot) {
		this.timeStamp = LocalDateTime.now();
		this.node_Snapshot = node_Snapshot;
	}
	
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "VersionNode [timeStamp=" + timeStamp + ", node_Snapshot="
				+ node_Snapshot + "]";
	}
	
}
