package com.github.fru.torsion.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.github.fru.torsion.jquery.JQuery;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class Main_JQuery {

	static JQuery jQuery = getHtmlDocument();

	public static void main(String[] args) throws Exception {
		double time = System.currentTimeMillis();
		
		jQuery.get("body").attr("id", "body");

		int count = 500;
		
		StringBuilder divs = new StringBuilder();
		for (int i = 0; i < count; i++) {
			divs.append("<div id='");
			divs.append(i);
			divs.append("' />");
		}
		jQuery.get("body").append(divs.toString());

		jQuery.get("div").after("<test/>");
		jQuery.get("body").before("test");
		jQuery.get("body").append(jQuery.create("test#tes.te.t.e[tz=tz][z=z]"));

		System.out.println(jQuery.get("body > test#tes.te.t.e[tz=tz][z=z],div").toString());

		System.out.println(jQuery.get("div+test+div").toString());
		time = (System.currentTimeMillis()-time)/1000;
		
		System.out.println(JQuery.toString(jQuery.getDocument()));
		
		System.out.println(time);
		
	}

	public static JQuery getHtmlDocument() {
		JQuery jQuery = new JQuery(new DocumentImpl());
		Document doc = jQuery.getDocument();
		Element root = doc.createElement("html");
		Element head = doc.createElement("head");
		Element body = doc.createElement("body");
		doc.appendChild(root);
		root.appendChild(head);
		root.appendChild(body);
		return jQuery;
	}

	public static PrintWriter write(String name) throws IOException {
		String path = new File(".").getCanonicalPath();
		path += "\\src\\";
		path += Main_JQuery.class.getPackage().getName().replace('.', '\\');

		File file = new File(path + "\\" + name);
		if (!file.exists()) {
			file.createNewFile();
		}
		return new PrintWriter(new FileWriter(file, false));
	}
}
