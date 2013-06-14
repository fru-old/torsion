package com.github.fru.torsion.javascript;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.main.D;
import com.github.fru.torsion.main.E;

public class JsWriterModule extends JsWriterObject{
	
	public static void main(String... args) throws IOException{
		JsWriterModule torsion = new JsWriterModule();
		torsion.registerClass(E.class);
		PrintWriter out = new PrintWriter(System.out);
		torsion.write(out);
		out.flush();
	}
	
	public void write(PrintWriter out){
		out.println("(function(window){ /*global window*/");
		out.println("  \"use strict\";");
		this.writeObjects(out, this);
		
		//main.outTypeDefinition(out, main.getClass());
		out.println("})(window);");
		//JsDependencies.printMainFooter(out);
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
		}*/
	}
	
	private void iterate(ArrayList<Dependency> batch, HashSet<String> doneUrls, Dependency current){
		if(current != null && !doneUrls.contains(current.url)){
			batch.add(current);
			doneUrls.add(current.url);
			for(Dependency o : current.dependencies){
				iterate(batch, doneUrls, o);
			}
		}
	}
	
	/*
	 * Replacement
	 */

	@Override
	protected AccessibleObject replacement(AccessibleObject accessible) {
		return accessible;
	}
	
	@Override
	protected Class<?> replacement(Class<?> accessible) {
		if(accessible == Integer.class)return int.class;
		return accessible;
	}
}
