package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.normalization.Identifier;

public class JsWriterModule extends JsWriterObject{
	
	public void write(PrintWriter out){
		out.println("(function(window){ /*global window*/");
		out.println("  \"use strict\";");
		this.writeObjects(out, this);
			
		//main.outTypeDefinition(out, main.getClass());
		out.println("})(window);");
		//JsDependencies.printMainFooter(out);
		
		for(AccessibleObject ac : annotated){
			String action = ac.getAnnotation(Js.class).action();
			if(!"direct".equals(action) && !"load".equals(action))continue;
			if("load".equals(action)){
				out.println("(function(){");
				out.println("var oldOnload = window.onload;");
				out.println("window.onload=function(){");
				out.println("if(oldOnload)oldOnload();");
			}
			Member m = (Member)ac;
			out.print("new ");
			out.print(getName(m.getDeclaringClass()));
			out.print("().");
			out.print(getName(ac));
			out.println("();");
			if("load".equals(action)){
				
				out.println("};");
				out.println("})();");
			}
		}
	}
	
	/*
	 * Dependencies
	 */
	
	private final ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	
	public void add(Dependency dependency){
		this.dependencies.add(dependency);
	}
	
	public static class Dependency{
		int priority;
		String url;
		Dependency[] dependencies;
		
		public Dependency(int priority, String url, Dependency... dependencies){
			this.priority = priority;
			this.url = url;
			this.dependencies = dependencies;
		}
	}
	
	/*
	private void printMainFooter(PrintWriter out){
		Collections.sort(dependencies, new Comparator<Dependency>() {
			@Override
			public int compare(Dependency arg0, Dependency arg1) {
				Integer i = arg0.priority;
				return i.compareTo(arg1.priority);
			}
		});
		out.println("var regDependence = [];");
		
		HashSet<String> doneUrls = new HashSet<String>();
		ArrayList<Dependency> batch = new ArrayList<Dependency>();
		
		for(Dependency current : dependencies){
			iterate(batch, doneUrls, current);
			if(batch.size() == 0)continue;
			out.println("regDependence.push{[");
			for(int i = batch.size()-1; i >= 0; i-- ) {
				out.println("\""+batch.get(i).url+"\",");
			}
			out.println("]};");
			batch.clear();
		}
		
		/*try {
			//TODO no direct link
			String load = JsNaming.getName(Loader.class)+"."+JsNaming.getName(Loader.class.getMethod("load", String[][].class, Action.Arg0.class));
			String main = JsNaming.getName(Main.class)+"()."+JsNaming.getName(Main.class.getMethod("main"));
			if(dependencies.size() > 0){
				out.println(load+"(regDependence, new "+main+");");
			}else{
				out.println("new "+main+"();");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}*/
	
	/*private void iterate(ArrayList<Dependency> batch, HashSet<String> doneUrls, Dependency current){
		if(current != null && !doneUrls.contains(current.url)){
			batch.add(current);
			doneUrls.add(current.url);
			for(Dependency o : current.dependencies){
				iterate(batch, doneUrls, o);
			}
		}
	}*/
	
	/*
	 * Replacement
	 */
	
	public static HashMap<Class<?>, Class<?>> replacements = new HashMap<Class<?>, Class<?>>();

	@Override
	protected AccessibleObject replacement(AccessibleObject accessible) {
		AccessibleObject out = accessible;
		if(!(accessible instanceof Member))return accessible;
		Class<?> clazz = ((Member)accessible).getDeclaringClass();
		clazz = replacements.containsKey(clazz) ? replacements.get(clazz) : clazz;
		
		if(accessible instanceof Method){
			Method m = ((Method)accessible);
			try {
				out = clazz.getMethod(m.getName(), m.getParameterTypes());
			} catch (Exception e) {
			}
		}else if(accessible instanceof Constructor<?>){
			Constructor<?> m = ((Constructor<?>)accessible);
			try {
				out = clazz.getConstructor(m.getParameterTypes());
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		Js a = accessible.getAnnotation(Js.class);
		if(out instanceof Member){
			a = ((Member)out).getDeclaringClass().getAnnotation(Js.class);
		}
		if(a == null){
			throw new RuntimeException("Missing @Js  "+out);
		}
		
		return out;
	}
	
	@Override
	protected Class<?> replacement(Class<?> accessible) {
		Class<?> out = replacements.containsKey(accessible) ? replacements.get(accessible) : accessible;
		if(out.getAnnotation(Js.class)==null)throw new RuntimeException(out + " has no @Js Annotation.");
		return out;
	}
	
	@Override
	protected void replace(Identifier identifier){
		ArrayList<Class<?>> classes = identifier.type.getClasses();
		if(classes != null)
		for(int i = 0; i < classes.size(); i++){
			if(classes.get(i)!=void.class)classes.set(i, replacement(classes.get(i)));
		}
	}
}
