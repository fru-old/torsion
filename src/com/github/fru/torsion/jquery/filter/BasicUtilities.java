package com.github.fru.torsion.jquery.filter;

import java.util.HashSet;

import org.w3c.dom.Node;

import com.github.fru.torsion.jquery.AttributeUtilities;

public class BasicUtilities {

	protected static HashSet<String> parseClasses(String classes) {
		HashSet<String> out = new HashSet<String>();
		if (classes != null) for (String s : classes.replaceAll("\\s", ",").split(",")) {
			if (s.length() > 0) out.add(s);
		}
		return out;
	}

	protected static String unparseClasses(HashSet<String> classes) {
		String out = "";
		for (String s : classes)
			out += "," + s;
		return out.length() > 0 ? out.substring(1) : out;
	}

	public static void addClasses(Node node, String classes) {
		HashSet<String> before = parseClasses(AttributeUtilities.getAttribute(node, "class"));
		HashSet<String> after = parseClasses(classes);
		after.addAll(before);
		AttributeUtilities.setAttribute(node, "class", unparseClasses(after));
	}

}
