package com.github.fru.torsion.lib.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class DomUtilities {
	
	/*****************************************************
	 * Serialization
	 *****************************************************/
	
	public static ArrayList<Node> createNodes(Document doc, String content){
		ArrayList<Node> out = new ArrayList<Node>();
		try{
			DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = bf.newDocumentBuilder();
			Document add = b.parse(new ByteArrayInputStream( ("<f>"+content+"</f>").getBytes() ));
			NodeList l = add.getFirstChild().getChildNodes();
			for(int i = 0; i < l.getLength(); i++){
				out.add(doc.importNode(l.item(i), true));
			}
		}catch(Exception e){
			return new ArrayList<Node>();
		}
		return out;
	}
	
	public static String toString(Document doc) throws IOException{
		OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);
        return out.toString();
	}
	
	public static ArrayList<String> deserializeClasses(String string){
		if(string == null)return new ArrayList<String>();
		string = string.replace(' ', ',');
		string = string.replace('\t', ',');
		ArrayList<String> out = new ArrayList<String>();
		for(String s : string.split(",")){
			s = s.trim();
			if(s.length()>0)out.add(s);
		}
		return out;
	}
	
	public static void addClasses(Iterable<Node> collection, String string){
		ArrayList<String> classes = DomUtilities.deserializeClasses(string);
		for(Node e : collection){
			ArrayList<String> existing = DomUtilities.deserializeClasses(DomUtilities.getAttribute(e, "class"));
			existing.addAll(classes);
			DomUtilities.setAttribute(e, "class", DomUtilities.serializeClasses(existing));
		}
	}
	
	public static String serializeClasses(Iterable<String> classes){
		String out = "";
		for(String s : classes)
			out += ","+s;
		
		if(out.length()>0)return out.substring(1);
		return out;
	}
	
	/*****************************************************
	 * Dom manipulation functionality 
	 *****************************************************/
	
	public static Element createNodeFromSelector(Document document, String selector){
		try {
			return new Selector(document, selector).build();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static void after(Iterable<Node> list, Iterable<Node> insert){
		boolean first = true;
		for(Node current : list ){
			Node parent = current.getParentNode();
			Node last = current.getNextSibling();
			if(parent != null){
				if(!first)insert = clone(insert);
				for(Node i : insert){
					parent.insertBefore(i, last);
				}	
			}
			first = false;
		}
	}
	
	public static Iterable<Node> clone(Iterable<Node> iterable){
		ArrayList<Node> out = new ArrayList<Node>();
		for(Node n: iterable){
			out.add(n.cloneNode(true));
		}
		return out;
	}

	public static void before(Iterable<Node> list, Iterable<Node> insert){
		boolean first = true;
		for(Node current : list ){
			Node parent = current.getParentNode();
			if(parent != null){
				if(!first)insert = clone(insert);
				for(Node i : insert){
					parent.insertBefore(i, current);
				}	
			}
			first = false;
		}
	}
	
	public static void remove(Node current){
		if(current != null && current.getParentNode() != null){
			current.getParentNode().removeChild(current);
		}
	}

	/*****************************************************
	 * Dom traversal functionality 
	 *****************************************************/
	
	public static String getAttribute(Node node, String string){
		Node attribute = node.getAttributes() != null ? node.getAttributes().getNamedItem(string) : null;
		return attribute != null ? attribute.getNodeValue() : null;
	}
	
	public static void setAttribute(Node node, String string, String value){
		if(node instanceof Element){
			((Element)node).setAttribute(string, value);
		}
	}
	
	/*****************************************************
	 * Functionality for a css like selector.
	 *****************************************************/
	
	private static final String DESCENDENT = " ";
	private static final String CHILD = ">";
	private static final String ADJACENT = "+";
	private static final String SIBLING = "~";
	private static final String SPLIT = ",";
	
	private static String getSeperator(int c){
		if(c == CHILD.charAt(0))return CHILD;
		if(c == ADJACENT.charAt(0))return ADJACENT;
		if(c == SIBLING.charAt(0))return SIBLING;
		if(c == SPLIT.charAt(0))return SPLIT;
		return null;
	} 
	
	private static String readBreaket(StringReader in, int open, int close) throws IOException{
		String out = ""+(char)open;
		int count = 1;
		while(count > 0){
			int c = in.read();
			if(c == -1)throw new IOException("Eof, but breaket not closed.");
			out += (char)c;
			if(c == open)count++;
			if(c == close)count--;
		}
		return out;
	}
	
	public static LinkedHashSet<Node> get(Document document, String string){
		try{
			ArrayList<String> splits = new ArrayList<String>();
			StringReader in = new StringReader(string);
			String current = "";
			while(true){
				int c = in.read();
				if(getSeperator(c)!=null){
					current = current.trim();
					if(current.length() > 0)
						splits.add(current);
					splits.add(getSeperator(c));
					current = "";
					continue;
				}else if(c == -1){
					break;
				}else if(c <= ' '){
					current = current.trim();
					if(current.length() > 0)
						splits.add(current);
					current = "";
					continue;
				}else if(c == '{'){
					current += readBreaket(in, '{', '}');
					continue;
				}else if(c == '['){
					current += readBreaket(in, '[', ']');
					continue;
				}
				current += (char)c;
			}
			current = current.trim();
			if(current.length() > 0)splits.add(current);
	
			List<String> seperators = new ArrayList<String>();
			List<Selector> selectors = new ArrayList<Selector>();
			for(int i = 0; i < splits.size(); i++){
				if(i % 2 == 0){//0,2,4...
					if(getSeperator(splits.get(i).charAt(0))==null){
						splits.add(i, DESCENDENT);
					}
					seperators.add(splits.get(i));
				}else{//1,3,5...
					if(getSeperator(splits.get(i).charAt(0))!=null)
						throw new IOException("Wrong placement of seperator.");
					selectors.add(new Selector(document, splits.get(i)));
				}
			}
			if(seperators.size() != selectors.size())throw new IOException("Unbalanced result.");
			
			LinkedHashSet<Node> out = new LinkedHashSet<Node>();
			while(seperators.contains(SPLIT)){
				int index = seperators.indexOf(SPLIT);
				if(index == 0)throw new IOException("Wrong position comma.");
				out.addAll(find(document, seperators.subList(0, index), selectors.subList(0, index)));
				seperators = seperators.subList(index, seperators.size());
				selectors = selectors.subList(index, selectors.size());
				seperators.set(0, DESCENDENT);
			}
			out.addAll(find(document, seperators, selectors));
			return out;
		}catch(Exception e){
			//Return empty result
		}
		return new LinkedHashSet<Node>();
	}
	
	private static final String CLASS = ".";
	private static final String ID = "#";
	private static final String CUSTOME = ":";
	
	private static String getSelector(int c){
		if(c == CLASS.charAt(0))return CLASS;
		if(c == ID.charAt(0))return ID;
		if(c == CUSTOME.charAt(0))return CUSTOME;
		if(c == DESCENDENT.charAt(0))return DESCENDENT;
		return null;
	}
	
	private static class Selector{
		Document document;
		ArrayList<Character> key = new ArrayList<Character>();
		ArrayList<String> value = new ArrayList<String>();
		
		private Selector(Document document, String selector) throws IOException{
			this.document = document;
			StringReader in = new StringReader(" "+selector);
			String current = "";
			while(true){
				int c = in.read();
				if(getSelector(c)!=null || c == '[' || c == '(' || c == -1){
					if(current.length()>0){
						key.add(current.charAt(0));
						value.add(current.substring(1));
					}
					current = "";
				}
				if(c == '[' || c == '('){
					char close = c == '[' ? ']' : ')';
					key.add((char)c);
					value.add(readBreaket(in, c, close));
					continue;
				}
				if(c == -1){
					break;
				}
				current += (char)c;
			}
		}
		
		private boolean match(Node node){
			try{
				if(document == null || node.getNodeType() != Node.ELEMENT_NODE)return false;
				for(int i = 0; i < key.size(); i++){
					char c = key.get(i).charValue();
					if(c == ' ' && value.get(i).length()>0 && !value.get(i).equals(node.getNodeName()) && !value.get(i).equals("*"))return false;
					if(c == '#' && !value.get(i).equals(DomUtilities.getAttribute(node, "id")))return false;
					if(c == '.'){
						boolean found = false;
						String[] klassen = value.get(i).split(",");
						for(String klasse : klassen){
							if(klasse.trim().equals(value.get(i))){
								found = true;
							}
						}
						if(!found)return false;
					}
					if(c == '['){
						String full = value.get(i);
						int eq = full.indexOf('=');
						String value = full.substring(eq+1, full.length()-1);
						String key = full.substring(1,eq);
						char sign = key.charAt(key.length()-1);
						if(sign == '|' || sign == '*' || sign == '~' || sign == '$' || sign == '!' || sign == '^'){
							key = key.substring(0, key.length()-1);
						}
						String attribute = DomUtilities.getAttribute(node,key);
						
						if(sign == '|' && !attribute.startsWith(value+"-") && !attribute.equals(value))return false;
						if(sign == '*' && attribute.indexOf(value) == -1)return false;
						if(sign == '~' && attribute.indexOf(value+" ") == -1 && attribute.indexOf(" "+value) == -1)return false;
						if(sign == '$' && attribute.endsWith(value))return false;
						if(sign == '!' && attribute.equals(value))return false;
					}
				}
			}catch(Exception e){
				return false;
			}
			return true;
		}
		
		private Element build(){
			if(key.size() == 0 || key.get(0).equals(DomUtilities.DESCENDENT) || value.get(0).length() == 0 )return null;
			Element out = document.createElement(value.get(0));
			String classes = "";
			for(int i = 1; i < key.size(); i++){
				switch(key.get(i).charValue()){
				case '#': 
					out.setAttribute("id", value.get(i));
					break;
				case '.':
					classes += ","+value.get(i);
					break;
				case '[':
					String full = value.get(i);
					int eq = full.indexOf('=');
					String value = full.substring(eq+1, full.length()-1);
					String key = full.substring(1,eq);
					out.setAttribute(key, value);
					break;
				default:
					return null;
				}
			}
			if(classes.length() > 0)out.setAttribute("class", classes.substring(1));
			return out; 
		}
	}
	
	private static LinkedHashSet<Node> find(Node current, List<String> seperators, List<Selector> selectors) throws IOException{
		if(seperators == null || selectors == null || seperators.size() != selectors.size() || seperators.size() == 0)return null;
		String seperator = seperators.get(0);
		Selector selector = selectors.get(0);
		List<String> subSeperators = seperators.subList(1, seperators.size());
		List<Selector> subSelectors = selectors.subList(1, selectors.size());
		LinkedHashSet<Node> out = new LinkedHashSet<Node>();
		if(seperator == DESCENDENT || seperator == CHILD){
			NodeList direct = current.getChildNodes();
			for(int i = 0; i < direct.getLength(); i++){
				if(selector.match(direct.item(i))){
					if(subSelectors.size() == 0){
						out.add(direct.item(i));
					}else{
						out.addAll(find(direct.item(i),subSeperators, subSelectors));
					}
				}
				if(seperator == DESCENDENT){
					out.addAll(find(direct.item(i),seperators,selectors));
				}
			}
		}else if(seperator == SPLIT){
			throw new IOException("Unexpected comma.");
		}else if(seperator == ADJACENT){
			Node adjacent = current.getNextSibling();
			if(adjacent != null && selector.match(adjacent)){
				if(subSelectors.size() == 0){
					out.add(adjacent);
				}else{
					out.addAll(find(adjacent,subSeperators, subSelectors));
				}
			}
		}else if(seperator == SIBLING){
			Node sibling = current.getPreviousSibling();
			if(sibling == null){
				sibling = current.getNextSibling(); 
			}else{
				while(sibling.getPreviousSibling() != null)
					sibling = sibling.getPreviousSibling();
			}
			while(sibling != null){
				if(selector.match(sibling)){
					if(subSelectors.size() == 0){
						out.add(sibling);
					}else{
						out.addAll(find(sibling,subSeperators, subSelectors));
					}
				}
				sibling = sibling.getNextSibling();
			}
		}
		return out;
	}
}
