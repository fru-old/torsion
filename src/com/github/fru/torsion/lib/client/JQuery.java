package com.github.fru.torsion.lib.client;

import java.io.IOException;
import java.util.HashSet;

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
		JQueryCollection jQuery = new JQueryCollection();
		jQuery.collection = new HashSet<Node>();
		jQuery.collection.addAll(collection.collection);
		return jQuery;
	}
	
	public JQueryCollection create(String content){
		JQueryCollection out = new JQueryCollection();
		out.collection = new HashSet<Node>();
		out.collection.add(DomUtilities.createNodeFromSelector(getDocument(), content));
		return out;
	}
	
	@Override
	public String toString(){
		try {
			return DomUtilities.toString(getDocument());
		} catch (IOException e) {
			return "";
		}
	}
	
	public class JQueryCollection{
		HashSet<Node> collection = new HashSet<Node>();
		
		@Override
		public String toString(){
			String out = "";
			for(Node n : collection){
				String id = DomUtilities.getAttribute(n, "id");
				out += n.getNodeName()+(id == null ? "" : "#"+id)+",";
			}
			return out.substring(0, out.length()-1);
		}
		
		public JQueryCollection add(String content){
			this.collection.addAll(DomUtilities.get(getDocument(), content));
			return this;
		}
		
		public JQueryCollection add(JQueryCollection content){
			this.collection.addAll(content.collection);
			return this;
		}
	
		public JQueryCollection addClass(String string){
			DomUtilities.addClasses(collection, string);
			return this;
		}
		
		//Add nodes as siblings
		public JQueryCollection after(String content){
			DomUtilities.after(collection, DomUtilities.createNodes(getDocument(), content));
			return this;
		}
		
		public JQueryCollection after(JQueryCollection content){
			DomUtilities.after(collection, content.collection);
			return this;
		}
		
		public JQueryCollection before(String content){
			DomUtilities.before(collection, DomUtilities.createNodes(getDocument(), content));
			return this;
		}
		
		public JQueryCollection before(JQueryCollection content){
			DomUtilities.before(collection, content.collection);
			return this;
		}
		
		//Add nodes but reverse content and destination
		/*public JQueryCollection appendTo(JQueryCollection destination){
			destination.append(this);
		}*/
		
		//Attribute
		public JQueryCollection attr(String key, String value){
			for(Node t: this.collection)DomUtilities.setAttribute(t, key, value);
			return this;
		}
		
		
		//Add nodes as childreen
		public JQueryCollection append(JQueryCollection content){
			for(Node t: this.collection)for(Node n: content.collection)t.appendChild(n);
			return this;
		}
		
		public JQueryCollection append(String content){
			for(Node t: this.collection)for(Node n: DomUtilities.createNodes(getDocument(), content))t.appendChild(n);
			return this;
		}
	}
	
}