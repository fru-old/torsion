package com.github.fru.torsion.jquery.selector;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.CssToken;

public abstract class AbstractSelector{
	
	public LinkedHashSet<Node> match(LinkedHashSet<Node> nodes) {
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for(Node node : nodes)if(match(node))out.add(node);
		return out;
	}
	
	public boolean match(Node node){
		throw new UnsupportedOperationException("Does not match simple node.");
	}
	
	public void make(Node node){
		throw new UnsupportedOperationException("Does not match simple node.");
	}
	
	/*private static AbstractSelector getAllSelector(){
		return new BasicSelector(new CssToken('t', "*"));
	}*/
	
	public static HashSet<Node> filter(List<CssToken> selectors, LinkedHashSet<Node> roots){
		return filter(roots, roots, selectors);
	}
	
	public static void make(CssToken current, CssToken next, Node out){
		build(current, next).make(out);
	}
	
	private static AbstractSelector build(CssToken current, CssToken next){
		if(current.type == '#' || current.type == '.' || current.type == 't'  || current.type == '['){
			return new BasicSelector(current);
		}else if(current.type == ':'){
			return new CustomSelector(current, next);
		}else if(current.type == 'd' || current.type == '>' || current.type == '+' || current.type == '~'){
			return new HierarchySelector(current);
		}else{
			return null;
		}
	}
	
	private static LinkedHashSet<Node> filter(LinkedHashSet<Node> original, LinkedHashSet<Node> out, List<CssToken> list){
		if(list.size() == 0)return out;
		
		CssToken current = list.get(0);
		CssToken next = list.size()>1 ? list.get(1) : null;
		list = list.subList(1, list.size());
	
		if(current.type == ','){
			out = new LinkedHashSet<Node>(out);
			out.addAll(filter(original, original, list));
		}else{
			AbstractSelector selector = AbstractSelector.build(current, next);
			if(selector != null)out = filter(original, selector.match(out), list);
		}
		return out;
	}
}