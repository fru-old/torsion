package com.github.fru.torsion.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.github.fru.torsion.javascript.JsWriterModule;

public class Generator {
	
	public static void registerDefaultTypes(){
		JsWriterModule.replacements.put(Object.class, Buildin.Object.class);
		JsWriterModule.replacements.put(Integer.class, Buildin.Number.class);
		JsWriterModule.replacements.put(int.class, Buildin.Number.class);
		JsWriterModule.replacements.put(String.class, Buildin.String.class);
		JsWriterModule.replacements.put(StringBuilder.class, Buildin.String.class);
		JsWriterModule.replacements.put(Boolean.class, Buildin.Boolean.class);
		JsWriterModule.replacements.put(boolean.class, Buildin.Boolean.class);
	}

	public static File generateSimpleFiles(Class<?> clazz) throws IOException{
		File html = File.createTempFile("index", ".html");
		File js = File.createTempFile("torsion", ".js");
		
		registerDefaultTypes();
		
		JsWriterModule torsion = new JsWriterModule();
		torsion.registerClass(clazz);
		PrintWriter out = new PrintWriter(js);
		torsion.write(out);
		out.flush();
		
		out = new PrintWriter(html);
		out.println("<html>");
		out.println("<head>");
		out.println("<script type=\"text/javascript\" src=\""+js.toURI().toString()+"\">");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		
		out.flush();
		
		return html;
	}
	
	public static void printSimpleJavascript(Class<?> clazz) throws IOException{
		registerDefaultTypes();
			
		JsWriterModule torsion = new JsWriterModule();
		torsion.registerClass(clazz);
		PrintWriter out = new PrintWriter(System.out);
		torsion.write(out);
		out.flush();
	}
}
