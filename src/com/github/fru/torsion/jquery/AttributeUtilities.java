package com.github.fru.torsion.jquery;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AttributeUtilities {

	public static boolean hasAttribute(Node node, String name){
		if(node.getAttributes()==null)return false;
		return node.getAttributes().getNamedItem(name) != null;
	}
	
	public static void setAttribute(Node node, String name, String value){
		if(!(node instanceof Element))return;
		((Element)node).setAttribute(name, value);
	}
	
	public static String getAttribute(Node node, String name){
		if(!(node instanceof Element))return null;
		return ((Element)node).getAttribute(name);
	}
}
