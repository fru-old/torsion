package com.github.fru.torsion.jquery.utils;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class InsertionUtilities {

	public static void after(Iterable<Node> list, Iterable<Node> insert) {
		boolean first = true;
		for (Node current : list) {
			Node parent = current.getParentNode();
			Node last = current.getNextSibling();
			if (parent != null) {
				if (!first) insert = clone(insert);
				for (Node i : insert) {
					parent.insertBefore(i, last);
				}
			}
			first = false;
		}
	}

	public static Iterable<Node> clone(Iterable<Node> iterable) {
		ArrayList<Node> out = new ArrayList<Node>();
		for (Node n : iterable) {
			out.add(n.cloneNode(true));
		}
		return out;
	}

	public static void before(Iterable<Node> list, Iterable<Node> insert) {
		boolean first = true;
		for (Node current : list) {
			Node parent = current.getParentNode();
			if (parent != null) {
				if (!first) insert = clone(insert);
				for (Node i : insert) {
					parent.insertBefore(i, current);
				}
			}
			first = false;
		}
	}

	public static void remove(Node current) {
		if (current != null && current.getParentNode() != null) {
			current.getParentNode().removeChild(current);
		}
	}

}
