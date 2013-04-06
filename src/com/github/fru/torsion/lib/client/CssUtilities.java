package com.github.fru.torsion.lib.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.fru.torsion.lib.client.SelectorUtilities.Selector;

public class CssUtilities {

	public static HashSet<String> parseClasses(String classes){
		HashSet<String> out = new HashSet<String>();
		if(classes != null)
		for(String s : classes.replaceAll("\\s", ",").split(",")){
			if(s.length() > 0)out.add(s);
		}
		return out;
	}
	
	public static String toString(HashSet<String> classes){
		String out = "";
		for(String s : classes)out += ","+s;
		return out.length() > 0 ? out.substring(1) : out;
	}
	
	public static void addClasses(Node node, String classes){
		HashSet<String> before = parseClasses(NodeUtilities.getAttribute(node, "class"));
		HashSet<String> after = parseClasses(classes);
		after.addAll(before);
		NodeUtilities.setAttribute(node, "class", toString(after));
	}
	
	public static class CssToken{
		public int type;
		public String content;
		
		public CssToken(int type, String content){
			this.type = type;
			this.content = content;
		}
	}
	
	public static ArrayList<CssToken> parseSelector(String selector){
		ArrayList<CssToken> out = new ArrayList<CssToken>();
		if(selector == null)return out;
		
		byte[] array = selector.trim().getBytes(Charset.forName("US-ASCII"));
		PushbackInputStream stream = new PushbackInputStream(new ByteArrayInputStream(array));
		
		try{
			while(true){
				if(parseSpaces(stream))out.add(new CssToken('d', ""));
				int c = stream.read();
				if(c == -1)return out;
				
				if(isBreaket(c)){
					out.add(new CssToken(c, parseBreaket(stream, c)));
				}else if(isHierarchy(c)){
					out.add(new CssToken(c, ""));
					parseSpaces(stream);
				}else if(isSeperator(c)){
					out.add(new CssToken(c, parseSeperator(stream)));
				}else{
					stream.unread(c);
					out.add(new CssToken('t', parseSeperator(stream)));
				}
			}
		}catch(IOException e){
			return new ArrayList<CssToken>();
		}
	}
	
	private static boolean isSeperator(int type){
		return type == '#' || type == '.' || type == ':';
	}
	
	private static boolean isHierarchy(int type){
		return type == '>' || type == ',' || type == '+' || type == '~';
	}
	
	private static boolean isBreaket(int type){
		return type == '(' || type == '[';
	}
	
	private static String parseBreaket(PushbackInputStream stream, int opening) throws IOException{
		StringBuilder out = new StringBuilder();
		int closing = (opening == '[' ? ']' : ')');
		int depth = 1;
		while(depth > 0){
			int c = stream.read();
			if(c == -1)throw new IOException("Unexpected end of stream while parsing breaket");
			if(c == opening)depth++;
			if(c == closing)depth--;
			out.append((char)c);
		}
		return out.substring(0, out.length()-1);
	}
	
	private static String parseSeperator(PushbackInputStream stream) throws IOException{
		StringBuilder out = new StringBuilder();
		int c;
		do{
			c = stream.read();
			out.append((char)c);
		}while(c != -1 && !isSeperator(c) && !isBreaket(c) && !isHierarchy(c) && c > ' ');
		if(c != -1)stream.unread(c);
		return out.substring(0, out.length()-1);
	}
	
	private static boolean parseSpaces(PushbackInputStream stream) throws IOException{
		int c = stream.read();
		boolean foundSpaces = c>=0 && c<=' ';
		while(c>=0 && c<=' ')c = stream.read();
		if(c == -1)return false;
		stream.unread(c);
		return !isHierarchy(c) && foundSpaces;
	}
	
	public static LinkedHashSet<Node> filter(LinkedHashSet<Node> original, LinkedHashSet<Node> out, List<CssToken> list){
		if(list.size() == 0)return out;
		
		CssToken current = list.get(0);
		CssToken next = list.size()>1 ? list.get(1) : null;
		list = list.subList(1, list.size());
	
		if(current.type == ','){
			out = new LinkedHashSet<Node>(out);
			out.addAll(filter(original, original, list));
		}else{
			Selector selector = getSelectors(current, next);
			if(selector != null)out = filter(original, selector.match(out), list);
		}
		return out;
	}
	
	public static Selector getSelectors(CssToken current, CssToken next){
		if(current.type == '#' || current.type == '.' || current.type == 't'){
			return new SelectorUtilities.BasicSelector(current);
		}else if(current.type == ':'){
			return new SelectorUtilities.CustomSelector(current, next);
		}else if(current.type == '['){
			return new SelectorUtilities.AttributeSelector(current);
		}else if(current.type == 'd' || current.type == '>' || current.type == '+' || current.type == '~'){
			return new SelectorUtilities.HirarchySelector(current);
		}else{
			return null;
		}
	}
	
	public static HashSet<Node> get(List<CssToken> list, Document doc){
		LinkedHashSet<Node> original = NodeUtilities.appendTagChildreen(doc, true, new LinkedHashSet<Node>());
		return filter(original, original, list);
	}
	
	public static Node create(List<CssToken> list, Document doc){
		String tag = "div";
		for(CssToken t : list)if(t.type == 't')tag = t.content;
		Node out = doc.createElement(tag);
		while(list.size() > 0){
			getSelectors(list.get(0), list.size()>1 ? list.get(1) : null).make(out);
			list = list.subList(1, list.size());
		}
		return out;
	}
	
	public static void main(String[] args){
		String selector = "#test test.t#test + *#test:";
		ArrayList<CssToken> sperated = parseSelector(selector);
		for(CssToken t : sperated){
			System.out.println((char)t.type+":"+t.content);
		}
	}
}
