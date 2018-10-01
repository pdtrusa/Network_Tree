package com.fujitsu.fnc.sdnfw.vidya.mula.restApi;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name = "executeRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonRootName(value = "executeRequest")
public class RestInputRequest {

	@XmlElement(name="operName", required = true)
	private String operName;
	
	@XmlElement(name="inputCmds")
	private List<RestInput> inputCmds;

	public String getOperName() {
		return operName;
	}

	public void setOperName(String operName) {
		this.operName = operName;
	}

	public List<RestInput> getInputCmds() {
		return inputCmds;
	}

	public void setInputCmds(List<RestInput> inputCmds) {
		this.inputCmds = inputCmds;
	}

	@Override
	public String toString() {
		return "Request [operName=" + operName + ", inputCmds="
				+ inputCmds + "]";
	}
}

