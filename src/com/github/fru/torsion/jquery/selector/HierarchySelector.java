package com.github.fru.torsion.jquery.selector;

import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.CssToken;
import com.github.fru.torsion.jquery.TraversalUtilities;

public class HierarchySelector extends AbstractSelector{
	private CssToken token;
	
	public HierarchySelector(CssToken token){
		this.token = token;
	}

	@Override
	public LinkedHashSet<Node> match(LinkedHashSet<Node> nodes) {
		if(token.type == '>')return TraversalUtilities.getChildreen(nodes);
		if(token.type == 'd')return TraversalUtilities.getDescendants(nodes);
		if(token.type == '+')return TraversalUtilities.getNext(nodes);
		if(token.type == '~')return TraversalUtilities.getNextAll(nodes);
		return new LinkedHashSet<Node>();
	}

	@Override
	public void make(Node node) {}
}