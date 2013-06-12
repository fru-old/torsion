package com.github.fru.torsion.buildin;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.example.Main;
import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.main.TorsionOut;
import com.github.fru.torsion.main.TorsionOutDefault;

public class JsOutDefault extends TorsionOutDefault{
	
	public static TorsionOut main = new Main();

	public JsOutDefault(){
		super(Js.class);
	}
	
	@Override
	public void outBeforePackage(PrintWriter out, Class<?> clazz) {
		//Do nothing when there are no dependencies 
	}

	@Override
	public void outTypeDefinition(PrintWriter out, Class<?> clazz) {
		String clazzname = JsNaming.getName(clazz);
		String parent = JsNaming.getName(clazz.getSuperclass());
		out.println("function "+clazzname+"(){}");
		out.println(clazzname + ".prototype = new "+parent+"();");
		//method A.prototype.toString = function()...

	}

	@Override
	public void outBodyDefinition(PrintWriter out, AccessibleObject accessable, Body body) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outCallDefinition(PrintWriter out, AccessibleObject accessable,  AccessibleObject called,
			Identifier... identifier) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void before(PrintWriter out){
		out.println("(function(window){ /*global window*/");
		out.println("  \"use strict\";");
	}
	
	@Override
	public void after(PrintWriter out){
		main.outTypeDefinition(out, main.getClass());
		out.println("})(window);");
		JsDependencies.printMainFooter(out);
	}
	
}
