package com.github.fru.torsion.jquery.filter;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.w3c.dom.Node;

@SuppressWarnings("unused")
class CustomSelector extends AbstractSelector {
	private CssToken token = null;
	private CssToken parameter = null;

	protected CustomSelector(CssToken current, CssToken next) {
		if (current.type == ':') token = current;
		if (next.type == '(') parameter = next;
	}

	@Override
	protected Collection<Node> match(Collection<Node> nodes) {
		// TODO: implement custom selector
		return new LinkedHashSet<Node>();
	}

	@Override
	protected void make(Node node) {
	}
}