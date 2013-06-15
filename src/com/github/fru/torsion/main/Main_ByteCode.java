package com.github.fru.torsion.main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.example.Buildin;
import com.github.fru.torsion.javascript.JsWriterModule;


public class Main_ByteCode {

	
	public static void main1(String... args) throws Exception {
		HashMap<AccessibleObject, Body> result = ClassFile.parse(D.class);
		for (AccessibleObject o : result.keySet()) {
			System.out.println("+++ " + o + " +++");
			System.out.println(result.get(o).toString());
			System.out.println("--------------");
			//PrintWriter w = new PrintWriter(System.out);
			//JsBody.printBody(w, result.get(o), o);
			//w.flush();
			//System.out.println("");
		}
		
		/*
		 * for(int i = 0; i < 2; i++){
		 * System.out.println(toIdentifier(26*36*36-i)); }
		 */
	}
/*
	public static void main(String... args) {
		JsDependencies test1 = new JsDependencies(3, "test1");
		JsDependencies test2 = new JsDependencies(2, "test2", test1);
		JsDependencies test3 = new JsDependencies(1, "test3", test1, test2);

		JsDependencies.add(test1);
		JsDependencies.add(test2);
		JsDependencies.add(test3);

		PrintWriter writer = new PrintWriter(System.out);
		Torsion.parse(new JsOutDefault(), writer);

		writer.flush();
	}*/
	
	public static void main(String... args) throws IOException{
		boolean local = false;
		
		File html = File.createTempFile("index", ".html");
		File js = File.createTempFile("torsion", ".js");
		
		JsWriterModule.replacements.put(Object.class, Buildin.Object.class);
		JsWriterModule.replacements.put(Integer.class, Buildin.Number.class);
		JsWriterModule.replacements.put(int.class, Buildin.Number.class);
		JsWriterModule.replacements.put(String.class, Buildin.String.class);
		JsWriterModule.replacements.put(Boolean.class, Buildin.Boolean.class);
		JsWriterModule.replacements.put(boolean.class, Buildin.Boolean.class);
		
		JsWriterModule torsion = new JsWriterModule();
		torsion.registerClass(D.class);
		PrintWriter out = local ? new PrintWriter(System.out) : new PrintWriter(js);
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
		
		if(!local)Desktop.getDesktop().browse(html.toURI());
		
	}
}
