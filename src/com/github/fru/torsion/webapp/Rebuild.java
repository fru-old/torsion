package com.github.fru.torsion.webapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.fru.torsion.lib.client.JQuerySelector;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Rebuild {

	public static void main(String[] args) throws IOException {
		
		
		Document doc = new DocumentImpl();
		Element root = doc.createElement("html");
		Element body = doc.createElement("body");
		doc.appendChild(root);
		root.appendChild(body);
		body.setAttribute("id", "b");
		
		body.appendChild(doc.createTextNode("test test"));
		
		ArrayList<Node> list = new JQuerySelector().get(doc,"test");
		System.out.println(list.size());
		for(Node n : list){
			Node id = n.getAttributes() != null ? n.getAttributes().getNamedItem("id") : null;
			System.out.println("\t"+n.getNodeName()+(id == null ? "" : ("#"+id.getNodeValue())));
		}
		
		OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);

        //System.out.println(out.toString());
	}
	
	public static PrintWriter write(String name) throws IOException{
		String path = new File(".").getCanonicalPath();
		path += "\\src\\";
		path += Rebuild.class.getPackage().getName().replace('.', '\\');
		
		File file = new File(path +"\\"+ name);
		if(!file.exists()){
			file.createNewFile();
		}
		return new PrintWriter(new FileWriter(file, false));
	}
}
