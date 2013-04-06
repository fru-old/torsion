package com.github.fru.torsion.jquery;

import java.util.LinkedHashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TraversalUtilities {

	/*
	 * TODO: Discourse: Should traversal only return nodes of type element?
	 */

	/*
	 * TODO: Speed: The methods in this class are considered to perform well
	 * because in future releases they may be replaced by a speedy subclass of
	 * LinkedHashSet
	 */

	public static LinkedHashSet<Node> getAll(Document doc) {
		return TraversalUtilities.appendElementChildreen(doc, true, new LinkedHashSet<Node>());
	}

	public static LinkedHashSet<Node> getDescendants(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			TraversalUtilities.appendElementChildreen(node, true, out);
		}
		return out;
	}

	public static LinkedHashSet<Node> getChildreen(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			TraversalUtilities.appendElementChildreen(node, false, out);
		}
		return out;
	}
	
	private static LinkedHashSet<Node> appendElementChildreen(Node node, boolean deep, LinkedHashSet<Node> out) {
		NodeList list = node.getChildNodes();
		if (list != null) for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE){
				out.add(child);
				if (deep) appendElementChildreen(child, deep, out);
			}
		}
		return out;
	}

	public static LinkedHashSet<Node> getNext(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			if (node.getNextSibling() != null){
				node = node.getNextSibling();
				if(node.getNodeType() == Node.ELEMENT_NODE)out.add(node);
			}
		}
		return out;
	}

	public static LinkedHashSet<Node> getNextAll(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getNextSibling() != null) {
				node = node.getNextSibling();
				if(node.getNodeType() == Node.ELEMENT_NODE)out.add(node);
			}
		}
		return out;
	}

	public static LinkedHashSet<Node> getPrev(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			if (node.getPreviousSibling() != null){
				node = node.getPreviousSibling();
				if(node.getNodeType() == Node.ELEMENT_NODE)out.add(node);
			}
		}
		return out;
	}

	public static LinkedHashSet<Node> getPrevAll(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getPreviousSibling() != null) {
				node = node.getPreviousSibling();
				if(node.getNodeType() == Node.ELEMENT_NODE)out.add(node);
			}
		}
		return out;
	}

	public static LinkedHashSet<Node> getAncestors(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getParentNode() != null) {
				node = node.getParentNode();
				out.add(node);
			}
		}
		return out;
	}
	
	
}
