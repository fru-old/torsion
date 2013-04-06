package com.github.fru.torsion.lib.client;

import java.util.LinkedHashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.lib.client.CssUtilities.CssToken;

public class SelectorUtilities {

	public interface Selector{
		public LinkedHashSet<Node> match(Iterable<Node> nodes);
		public void make(Node node);
	}
	
	public static abstract class DefaultSelector implements Selector{
		
		@Override
		public LinkedHashSet<Node> match(Iterable<Node> nodes) {
			LinkedHashSet<Node> out = new LinkedHashSet<Node>();
			for(Node node : nodes)if(match(node))out.add(node);
			return out;
		}
		
		protected abstract boolean match(Node node);
	}
	
	public static class BasicSelector extends DefaultSelector{
		private CssToken token;
		
		public BasicSelector(CssToken token){
			this.token = token;
		}
		
		@Override
		protected boolean match(Node node){
			if(token == null)return false;
			if(token.type == 't'){
				return "*".equals(token.content) || node.getNodeName().equals(token.content);
			}else if(token.type == '#'){
				return token.content != null && token.content.equals(NodeUtilities.getAttribute(node, "id"));
			}else if(token.type == '.'){
				return CssUtilities.parseClasses(NodeUtilities.getAttribute(node, "class")).contains(token.content);
			}else{
				return false;
			}
		}

		@Override
		public void make(Node node) {
			if(token.type == '#'){
				NodeUtilities.setAttribute(node, "id", token.content);
			}else if(token.type == '.'){
				CssUtilities.addClasses(node, token.content);
			}
		}
	}
	
	public static class AttributeSelector extends DefaultSelector{
		private String key = null;
		private String value = null;
		private char sign = 0;
		
		public AttributeSelector(CssToken token){
			int eq = token.content.indexOf('=');
			if(eq == -1){
				key = token.content;
			}else{
				key = token.content.substring(0, eq);
				value = token.content.substring(eq+1);
				if(key.length()==0 || value.length() == 0){
					key = null;
					return;
				}
				
				if(value.length() > 2 && value.charAt(0) == '\'' && value.charAt(value.length()-1) == '\''){
					value = value.substring(1, value.length()-1);
				}
				
				sign = key.charAt(key.length()-1);
				if(sign == '|' || sign == '*' || sign == '~' || sign == '$' || sign == '!' || sign == '^'){
					key = key.substring(0, key.length()-1);
				}else{
					sign = 0;
				}
			}
		}
		
		@Override
		protected boolean match(Node node) {
			if(key == null)return false;
			if(value == null)return NodeUtilities.hasAttribute(node, key);
			
			String attribute = NodeUtilities.getAttribute(node,key);
			if(attribute == null)return false;
			
			//TODO: ~ not just matches spaces but all whitespace
			if(sign == '|')return attribute.startsWith(value+"-") || attribute.equals(value);
			else if(sign == '*')return attribute.contains(value);
			else if(sign == '~')return attribute.startsWith(value+" ") || attribute.endsWith(" "+value) || attribute.contains(" "+value+" ");
			else if(sign == '$')return attribute.endsWith(value);
			else if(sign == '!')return !attribute.equals(value);
			else return attribute.equals(value);
		}

		@Override
		public void make(Node node) {
			if(key == null || sign != 0)return;
			NodeUtilities.setAttribute(node, key, value!=null?value:"");
		}
	}
	
	public static class HirarchySelector implements Selector{
		private CssToken token;
		
		public HirarchySelector(CssToken token){
			this.token = token;
		}

		@Override
		public LinkedHashSet<Node> match(Iterable<Node> nodes) {
			LinkedHashSet<Node> out = new LinkedHashSet<Node>();
			if(token.type == '>'){
				for(Node node : nodes)NodeUtilities.appendTagChildreen(node, false, out);
			}else if(token.type == 'd'){
				for(Node node : nodes)NodeUtilities.appendTagChildreen(node, true, out);
			}else if(token.type == '+'){
				for(Node node : nodes){
					if(node.getNextSibling() != null)out.add(node.getNextSibling());
				}
			}else if(token.type == '~'){
				for(Node node : nodes){
					while(node.getNextSibling() != null){
						out.add(node.getNextSibling());
						node = node.getNextSibling();
					}
				}
			}
			return out;
		}

		@Override
		public void make(Node node) {}
	}
	
	@SuppressWarnings("unused")
	public static class CustomSelector implements Selector{
		private CssToken token = null;
		private CssToken parameter = null;
		
		public CustomSelector(CssToken current, CssToken next){
			if(current.type == ':')token = current;
			if(next.type == '(')parameter = next;
		}

		@Override
		public LinkedHashSet<Node> match(Iterable<Node> nodes) {
			//TODO: implement custom selector
			return new LinkedHashSet<Node>();
		}

		@Override
		public void make(Node node) {}
	}
	
	
}
