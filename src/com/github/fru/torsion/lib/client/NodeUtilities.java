package com.github.fru.torsion.lib.client;

import java.util.LinkedHashSet;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeUtilities {

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
	
	public static LinkedHashSet<Node> appendTagChildreen(Node node, boolean deep, LinkedHashSet<Node> out){
		NodeList list = node.getChildNodes();
		if(list != null)
		for(int i = 0; i < list.getLength(); i++){
			Node child = list.item(i);
			if(child.getNodeType() != Node.ELEMENT_NODE)continue;
			out.add(child);
			if(deep)appendTagChildreen(child, deep, out);
		}
		return out;
	}
}
