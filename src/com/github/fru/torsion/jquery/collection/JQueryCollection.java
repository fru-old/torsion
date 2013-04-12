package com.github.fru.torsion.jquery.collection;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.JQuery;
import com.github.fru.torsion.jquery.filter.Filter;
import com.github.fru.torsion.jquery.utils.AttributeUtilities;
import com.github.fru.torsion.jquery.utils.BasicUtilities;
import com.github.fru.torsion.jquery.utils.InsertionUtilities;
import com.github.fru.torsion.jquery.utils.TraversalUtilities;

public class JQueryCollection extends DomTraversal{

	/*
	 * TODO: Stack of recent collections, support for the following: -addBack,
	 * end, pushStack Produces new Stack: add TODO TASK DEF stack
	 */

	JQuery jQuery;
	Collection<Node> collection = new LinkedHashSet<Node>();

	public JQueryCollection(JQuery jQuery) {
		this.jQuery = jQuery;
	}

	public boolean contains(Node node) {
		return collection.contains(node);
	}

	// TOOO: remove - for experimental use only
	@Override
	public String toString() {
		String out = "";
		for (Node n : collection) {
			String id = AttributeUtilities.getAttribute(n, "id");
			out += n.getNodeName() + (id == null ? "" : "#" + id) + ",";
		}
		return out.length() > 0 ? out.substring(0, out.length() - 1) : "";
	}

	public JQueryCollection add(String content) {
		this.collection.addAll(new Filter(content).filter(TraversalUtilities.getAll(jQuery.getDocument())));
		return this;
	}

	public JQueryCollection add(JQueryCollection content) {
		this.collection.addAll(content.collection);
		return this;
	}
	
	public JQueryCollection add(Collection<Node> collection){
		this.collection.addAll(collection);
		return this;
	}
	
	public JQueryCollection add(Node node){
		this.collection.add(node);
		return this;
	}

	public JQueryCollection addClass(String string) {
		for (Node node : collection)
			BasicUtilities.addClasses(node, string);
		return this;
	}

	// Add nodes as siblings
	public JQueryCollection after(String content) {
		InsertionUtilities.after(collection, jQuery.parseHtml(content));
		return this;
	}

	public JQueryCollection after(JQueryCollection content) {
		InsertionUtilities.after(collection, content.collection);
		return this;
	}

	public JQueryCollection before(String content) {
		InsertionUtilities.before(collection, jQuery.parseHtml(content));
		return this;
	}

	public JQueryCollection before(JQueryCollection content) {
		InsertionUtilities.before(collection, content.collection);
		return this;
	}

	// Add nodes but reverse content and destination
	/*
	 * public JQueryCollection appendTo(JQueryCollection destination){
	 * destination.append(this); }
	 */

	// Attribute
	public JQueryCollection attr(String key, String value) {
		for (Node t : this.collection)
			AttributeUtilities.setAttribute(t, key, value);
		return this;
	}

	// Add nodes as childreen
	public JQueryCollection append(JQueryCollection content) {
		for (Node t : this.collection)
			for (Node n : content.collection)
				t.appendChild(n);
		return this;
	}

	public JQueryCollection append(String content) {
		for (Node t : this.collection)
			for (Node n : jQuery.parseHtml( content ))
				t.appendChild(n);
		return this;
	}
}