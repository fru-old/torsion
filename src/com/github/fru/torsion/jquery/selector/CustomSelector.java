package com.github.fru.torsion.jquery.selector;

import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.CssToken;

@SuppressWarnings("unused")
public class CustomSelector extends AbstractSelector{
	private CssToken token = null;
	private CssToken parameter = null;
	
	public CustomSelector(CssToken current, CssToken next){
		if(current.type == ':')token = current;
		if(next.type == '(')parameter = next;
	}

	@Override
	public LinkedHashSet<Node> match(LinkedHashSet<Node> nodes) {
		//TODO: implement custom selector
		return new LinkedHashSet<Node>();
	}

	@Override
	public void make(Node node) {}
}