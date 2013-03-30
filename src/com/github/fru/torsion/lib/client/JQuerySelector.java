package com.github.fru.torsion.lib.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JQuerySelector {

	private static final String DESCENDENT = " ";
	private static final String CHILD = ">";
	private static final String ADJACENT = "+";
	private static final String SIBLING = "~";
	private static final String SPLIT = ",";
	
	private static final String CLASS = ".";
	private static final String ID = "#";
	private static final String ALL = "*";
	private static final String CUSTOME = ":";
	
	private static final String OPEN_SQUARE = "[";
	private static final String OPEN_CURLY = "{";
	
	private String getSeperator(char c){
		if(c == CHILD.charAt(0))return CHILD;
		if(c == ADJACENT.charAt(0))return ADJACENT;
		if(c == SIBLING.charAt(0))return SIBLING;
		if(c == SPLIT.charAt(0))return SPLIT;
		if(c == DESCENDENT.charAt(0))return DESCENDENT;
		return null;
	} 
	
	private String getBreaket(char c){
		if(c == OPEN_CURLY.charAt(0))return OPEN_CURLY;
		if(c == OPEN_SQUARE.charAt(0))return OPEN_SQUARE;
		return null;
	}
	
	public ArrayList<Node> get(Document document, String string){
		ArrayList<String> splitSelector = new ArrayList<String>();
		int i = 0;
		string += " ";
		String current_full = " ";
		while(true){
			if(string.length() <= i)break;
			char c = string.charAt(i++);
			char current_type = current_full.charAt(0);
	
			if(getBreaket(current_type) != null){
				if( (OPEN_CURLY == getBreaket(current_type) && c == '}')
					|| (OPEN_SQUARE == getBreaket(current_type) && c == ']')){
					splitSelector.add(getBreaket(current_type));
					splitSelector.add(current_full.substring(1));
					current_full = "";
					c = string.charAt(i++);//skip this c
				}
			}else if(current_full.charAt(0) <= ' ' || getSeperator(current_type) != null){
				if(c > ' ' && getSeperator(c) == null){
					boolean onlyWhitespace = true;
					for(int j = 0; j < current_full.length(); j++){
						if(getSeperator(current_full.charAt(j)) != null){
							onlyWhitespace = false;
							splitSelector.add(getSeperator(current_full.charAt(j)));
						}
					}
					if(onlyWhitespace){
						splitSelector.add(DESCENDENT);
					}
					current_full = "";
				}
			}else{
				if(c <= ' ' || getSeperator(c)!=null || getBreaket(c) != null ){
					splitSelector.add(current_full);
					current_full = "";
				}
			}
			
			current_full += c;
		}
		splitSelector.add(current_full);
		splitSelector.remove(0);
		splitSelector.remove(splitSelector.size() -1);
		splitSelector.add(0, DESCENDENT);
		if(splitSelector.size() % 2 == 0){
			ArrayList<String> seperators = new ArrayList<String>();
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			for(int j = 0; j < splitSelector.size() / 2; j++){
				if(getSeperator(splitSelector.get(j*2).charAt(0)) == null)return new ArrayList<Node>();
				if(getSeperator(splitSelector.get(j*2+1).charAt(0)) != null)return new ArrayList<Node>();
				if(getBreaket(splitSelector.get(j*2+1).charAt(0)) != null)return new ArrayList<Node>();

				seperators.add(splitSelector.get(j*2));
				selectors.add(new Selector(document, splitSelector.get(j*2+1)));
			}
			return find(document, seperators, selectors);
		}
		return new ArrayList<Node>();
	}
	
	private class Selector{
		Document document;
		HashMap<String, String> found = new HashMap<String, String>();
		
		private Selector(Document document, String selector){
			this.document = document;
			System.out.println(selector);
		}
		
		private boolean match(Node node){
			return document != null;
		}
	}
	
	private ArrayList<Node> find(Node current, List<String> seperators, List<Selector> selectors){
		if(seperators == null || selectors == null || seperators.size() != selectors.size() || seperators.size() == 0)return null;
		String seperator = seperators.get(0);
		Selector selector = selectors.get(0);
		List<String> subSeperators = seperators.subList(1, seperators.size());
		List<Selector> subSelectors = selectors.subList(1, selectors.size());
		ArrayList<Node> out = new ArrayList<Node>();
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
		}
		return out;
	}
}
