package com.github.fru.torsion.jquery.filter;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.utils.TraversalUtilities;

class HierarchySelector extends AbstractSelector {
	private CssToken token;

	protected HierarchySelector(CssToken token) {
		this.token = token;
	}

	@Override
	protected Collection<Node> match(Collection<Node> nodes) {
		if (token.type == '>') return TraversalUtilities.getChildreen(nodes);
		if (token.type == 'd') return TraversalUtilities.getDescendants(nodes);
		if (token.type == '+') return TraversalUtilities.getNext(nodes);
		if (token.type == '~') return TraversalUtilities.getNextAll(nodes);
		return new LinkedHashSet<Node>();
	}

	@Override
	protected void make(Node node) {
	}
}