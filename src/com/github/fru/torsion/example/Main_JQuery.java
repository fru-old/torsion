package com.github.fru.torsion.example;

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
		jQuery.get("body").attr("id", "body");
		
		int count = 5;
		for(int i = 0; i < count; i++){
			jQuery.get("body").append("<div id='"+i+"' />");
		}
		
		jQuery.get("div").after("<test/>");
		jQuery.get("body").before("test");
		jQuery.get("body").append(jQuery.create("test#tes.te.t.e[tz=tz][z=z]"));
		
		System.out.println(jQuery.get("test").toString());
		
		System.out.println(jQuery.get("test#tes.te.t.e[tz=tz][z=z],div").toString());
		
		System.out.println(jQuery.get("div+test+div").toString());
		
        System.out.println(jQuery.toHtml());
	}

	public static JQuery getHtmlDocument(){
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
	
	public static PrintWriter write(String name) throws IOException{
		String path = new File(".").getCanonicalPath();
		path += "\\src\\";
		path += Main_JQuery.class.getPackage().getName().replace('.', '\\');
		
		File file = new File(path +"\\"+ name);
		if(!file.exists()){
			file.createNewFile();
		}
		return new PrintWriter(new FileWriter(file, false));
	}
}
