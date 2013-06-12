package com.github.fru.torsion.buildin;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.github.fru.torsion.example.Action;
import com.github.fru.torsion.example.Loader;
import com.github.fru.torsion.example.Main;

public class JsDependencies {
	
	int priority;
	String url;
	JsDependencies[] dependencies;
	
	public JsDependencies(int priority, String url, JsDependencies... dependencies){
		this.priority = priority;
		this.url = url;
		this.dependencies = dependencies;
	}
	
	private static final ArrayList<JsDependencies> registered = new ArrayList<JsDependencies>();
	public static void add(JsDependencies d){
		registered.add(d);
	}
	
	public static void printMainFooter(PrintWriter out){
		Collections.sort(registered, new Comparator<JsDependencies>() {
			@Override
			public int compare(JsDependencies arg0, JsDependencies arg1) {
				Integer i = arg0.priority;
				return i.compareTo(arg1.priority);
			}
		});
		out.println("var regDependence = [];");
		
		HashSet<String> doneUrls = new HashSet<String>();
		ArrayList<JsDependencies> batch = new ArrayList<JsDependencies>();
		
		for(JsDependencies current : registered){
			iterate(batch, doneUrls, current);
			if(batch.size() == 0)continue;
			out.println("regDependence.push{[");
			for(int i = batch.size()-1; i >= 0; i-- ) {
				out.println("\""+batch.get(i).url+"\",");
			}
			out.println("]};");
			batch.clear();
		}
		
		try {
			String load = JsNaming.getName(Loader.class)+"."+JsNaming.getName(Loader.class.getMethod("load", String[][].class, Action.Arg0.class));
			String main = JsNaming.getName(Main.class)+"()."+JsNaming.getName(Main.class.getMethod("main"));
			if(registered.size() > 0){
				out.println(load+"(regDependence, new "+main+");");
			}else{
				out.println("new "+main+"();");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private static void iterate(ArrayList<JsDependencies> batch, HashSet<String> doneUrls, JsDependencies current){
		if(current != null && !doneUrls.contains(current.url)){
			batch.add(current);
			doneUrls.add(current.url);
			for(JsDependencies o : current.dependencies){
				iterate(batch, doneUrls, o);
			}
		}
	}
}
