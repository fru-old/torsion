package com.github.fru.torsion.lib.client;

import java.io.IOException;
import java.util.LinkedHashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JQuery {
	
	Document doc = null;
	
	public JQuery(Document doc){
		this.doc = doc;
	}
	
	public Document getDocument(){
		return doc;
	}
	
	public JQueryCollection get(String content){
		return new JQueryCollection().add(content);
	}
	
	public JQueryCollection get(JQueryCollection collection){
		return new JQueryCollection().add(collection);
	}
	
	public JQueryCollection create(String content){
		JQueryCollection out = new JQueryCollection();
		out.collection.add(CssUtilities.create(CssUtilities.parseSelector(content), getDocument()));
		return out;
	}
	
	@Override
	public String toString(){
		try {
			return SerializationUtilities.toString(getDocument());
		} catch (IOException e) {
			return "";
		}
	}
	
	public class JQueryCollection{
		LinkedHashSet<Node> collection = new LinkedHashSet<Node>();
		
		@Override
		public String toString(){
			String out = "";
			for(Node n : collection){
				String id = NodeUtilities.getAttribute(n, "id");
				out += n.getNodeName()+(id == null ? "" : "#"+id)+",";
			}
			return out.substring(0, out.length()-1);
		}
		
		public JQueryCollection add(String content){
			this.collection.addAll(CssUtilities.get(CssUtilities.parseSelector(content), getDocument()));
			return this;
		}
		
		public JQueryCollection add(JQueryCollection content){
			this.collection.addAll(content.collection);
			return this;
		}
	
		public JQueryCollection addClass(String string){
			for(Node node : collection)CssUtilities.addClasses(node, string);
			return this;
		}
		
		//Add nodes as siblings
		public JQueryCollection after(String content){
			InsertionUtilities.after(collection, SerializationUtilities.createNodes(getDocument(), content));
			return this;
		}
		
		public JQueryCollection after(JQueryCollection content){
			InsertionUtilities.after(collection, content.collection);
			return this;
		}
		
		public JQueryCollection before(String content){
			InsertionUtilities.before(collection, SerializationUtilities.createNodes(getDocument(), content));
			return this;
		}
		
		public JQueryCollection before(JQueryCollection content){
			InsertionUtilities.before(collection, content.collection);
			return this;
		}
		
		//Add nodes but reverse content and destination
		/*public JQueryCollection appendTo(JQueryCollection destination){
			destination.append(this);
		}*/
		
		//Attribute
		public JQueryCollection attr(String key, String value){
			for(Node t: this.collection)NodeUtilities.setAttribute(t, key, value);
			return this;
		}
		
		
		//Add nodes as childreen
		public JQueryCollection append(JQueryCollection content){
			for(Node t: this.collection)for(Node n: content.collection)t.appendChild(n);
			return this;
		}
		
		public JQueryCollection append(String content){
			for(Node t: this.collection)for(Node n: SerializationUtilities.createNodes(getDocument(), content))t.appendChild(n);
			return this;
		}
	}
	
}