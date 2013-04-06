package com.github.fru.torsion.lib.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class SerializationUtilities {


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
	
}
