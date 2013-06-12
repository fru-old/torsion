package com.github.fru.torsion.example;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.github.fru.torsion.buildin.Js;
import com.github.fru.torsion.buildin.JsOutDefault;
import com.github.fru.torsion.buildin.JsNaming;
import com.github.fru.torsion.bytecode.normalization.Body;

@Js(global=true,name="Main")
public class Main extends JsOutDefault{
	
	ArrayList<String> codes = new ArrayList<String>();
	
	public void add(String code){
		codes.add(code);
	}
	
	public void add(AccessibleObject accessible){
		String method = JsNaming.getName(accessible.getClass())+"."+JsNaming.getName(accessible);
		add(method+"();");
	}
	
	@Js(global=true,name="main")
	public void main(){
		
	}
	
	@Override
	public void outBodyDefinition(PrintWriter out, AccessibleObject accessable, Body body) {
		if(accessable instanceof Method && ((Method)accessable).getName().equals("main")){
			for(String s : codes){
				out.println(s);
			}
		}
		throw new RuntimeException("Class can not be used this way");
	}
}
