package com.github.fru.torsion.jquery.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.collection.JQueryCollection;

public class Filter {

	private JQueryCollection collection = null;
	private ArrayList<CssToken> token = null;

	public Filter() {
		token = new ArrayList<CssToken>();
		token.add(new CssToken('t', "*"));
	}

	public Filter(String selector) {
		token = CssToken.parseSelector(selector.toString());
	}

	public Filter(JQueryCollection collection) {
		if (collection == null) throw new IllegalArgumentException("Collection can not be null.");
		this.collection = collection;
	}

	public Collection<Node> filter(Collection<Node> collection2) {
		if (token != null) return filter(collection2, collection2, token);
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		for (Node node : collection2) {
			if (collection.contains(node)) out.add(node);
		}
		return out;
	}

	public Node make(Document doc) {
		if (token == null) return null;
		String tag = "div";
		List<CssToken> list = token;
		for (CssToken t : list)
			if (t.type == 't') tag = t.content;
		Node out = doc.createElement(tag);
		while (list.size() > 0) {
			build(list.get(0), list.size() > 1 ? list.get(1) : null).make(out);
			list = list.subList(1, list.size());
		}
		return out;
	}

	private static AbstractSelector build(CssToken current, CssToken next) {
		if (current.type == '#' || current.type == '.' || current.type == 't' || current.type == '[') {
			return new BasicSelector(current);
		} else if (current.type == ':') {
			return new CustomSelector(current, next);
		} else if (current.type == 'd' || current.type == '>' || current.type == '+' || current.type == '~') {
			return new HierarchySelector(current);
		} else {
			return null;
		}
	}

	private static Collection<Node> filter(Collection<Node> original, Collection<Node> out, List<CssToken> list) {
		if (list.size() == 0) return out;

		CssToken current = list.get(0);
		CssToken next = list.size() > 1 ? list.get(1) : null;
		list = list.subList(1, list.size());

		if (current.type == ',') {
			out = new LinkedHashSet<Node>(out);
			out.addAll(filter(original, original, list));
		} else {
			AbstractSelector selector = Filter.build(current, next);
			if (selector != null) out = filter(original, selector.match(out), list);
		}
		return out;
	}

}
