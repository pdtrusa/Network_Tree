package com.fujitsu.fnc.sdnfw.vidya.mula.restApi;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RestInput {
	
	@XmlElement(name="inputCategory", required = true)
	private String inputCategory;
		
	@XmlElement(name="inputStrategy", required = true)
	private String inputStrategy;
		
	private Map<String, String> attributes;

	public String getInputCategory() {
		return inputCategory;
	}

	public void setInputCategory(String inputCategory) {
		this.inputCategory = inputCategory;
	}

	public String getInputStrategy() {
		return inputStrategy;
	}

	public void setInputStrategy(String inputStrategy) {
		this.inputStrategy = inputStrategy;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "RestInput [inputCategory=" + inputCategory + ", inputStrategy="
				+ inputStrategy + ", attributes=" + attributes + "]";
	}

	
}
