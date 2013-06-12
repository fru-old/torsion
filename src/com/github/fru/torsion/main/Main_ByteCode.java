package com.github.fru.torsion.main;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.util.HashMap;

import com.github.fru.torsion.buildin.JsBody;
import com.github.fru.torsion.buildin.JsDependencies;
import com.github.fru.torsion.buildin.JsOutDefault;
import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;

public class Main_ByteCode {

	public static void main1(String... args) throws Exception {
		HashMap<AccessibleObject, Body> result = ClassFile.parse(D.class);
		for (AccessibleObject o : result.keySet()) {
			System.out.println("+++ " + o + " +++");
			System.out.println(result.get(o).toString());
			System.out.println("--------------");
			PrintWriter w = new PrintWriter(System.out);
			JsBody.printBody(w, result.get(o), o);
			w.flush();
			System.out.println("");
		}
		
		/*
		 * for(int i = 0; i < 2; i++){
		 * System.out.println(toIdentifier(26*36*36-i)); }
		 */
	}

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
	}
}
