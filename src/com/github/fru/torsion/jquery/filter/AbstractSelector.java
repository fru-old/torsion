package com.github.fru.torsion.jquery.filter;

import java.util.LinkedHashSet;

import org.w3c.dom.Node;

abstract class AbstractSelector {

	protected LinkedHashSet<Node> match(LinkedHashSet<Node> nodes) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : nodes)
			if (match(node)) out.add(node);
		return out;
	}

	protected boolean match(Node node) {
		throw new UnsupportedOperationException("Does not match simple node.");
	}

	protected void make(Node node) {
		throw new UnsupportedOperationException("Does not match simple node.");
	}

}