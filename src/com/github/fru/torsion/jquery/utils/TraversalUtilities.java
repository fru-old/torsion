package com.github.fru.torsion.jquery.utils;

import java.util.Collection;
import java.util.Iterator;
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

	public static Collection<Node> getAll(final Document doc) {
		return new ImmutableCollectionProxy<Node>( new Iterable<Node>() {

			@Override
			public Iterator<Node> iterator() {
				return new ImmutableCollectionProxy.IterateAllNodes(doc);
			}
			
		});
	}

	public static Collection<Node> getDescendants(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			TraversalUtilities.appendElementChildreen(node, true, out);
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getChildreen(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			TraversalUtilities.appendElementChildreen(node, false, out);
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	private static Collection<Node> appendElementChildreen(Node node, boolean deep, LinkedHashSet<Node> out) {
		NodeList list = node.getChildNodes();
		if (list != null) for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				out.add(child);
				if (deep) appendElementChildreen(child, deep, out);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getNext(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			if (node.getNextSibling() != null) {
				node = node.getNextSibling();
				if (node.getNodeType() == Node.ELEMENT_NODE) out.add(node);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getNextAll(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getNextSibling() != null) {
				node = node.getNextSibling();
				if (node.getNodeType() == Node.ELEMENT_NODE) out.add(node);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getPrev(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			if (node.getPreviousSibling() != null) {
				node = node.getPreviousSibling();
				if (node.getNodeType() == Node.ELEMENT_NODE) out.add(node);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getPrevAll(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getPreviousSibling() != null) {
				node = node.getPreviousSibling();
				if (node.getNodeType() == Node.ELEMENT_NODE) out.add(node);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

	public static Collection<Node> getAncestors(Iterable<Node> current) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : current) {
			while (node.getParentNode() != null) {
				node = node.getParentNode();
				out.add(node);
			}
		}
		return new ImmutableCollectionProxy<Node>( out );
	}

}
