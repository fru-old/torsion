package com.github.fru.torsion.javascript;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public abstract class JsWriterObject extends JsWriterInstruction{
	
	/*
	 * Traversal
	 */

	protected final HashMap<Class<?>, HashMap<AccessibleObject, Body>> result = new HashMap<Class<?>, HashMap<AccessibleObject, Body>>();
	protected final LinkedHashSet<AccessibleObject> used = new LinkedHashSet<AccessibleObject>();
	protected final LinkedHashSet<AccessibleObject> annotated = new LinkedHashSet<AccessibleObject>();
	private final ArrayList<AccessibleObject> translate = new ArrayList<AccessibleObject>();
	
	public void registerClass(Class<?> clazz) throws IOException{
		for(Method m : clazz.getDeclaredMethods()){
			if(m.getAnnotation(Js.class) != null){
				translate.add(m);
			}
		}
		while(translate.size() > 0){
			AccessibleObject a = this.replacement(translate.remove(0));
			if(used.contains(a))continue;
			used.add(a);
			if(!(a instanceof Member))throw new RuntimeException(a+" is no Member");
			Member memA = (Member)a;
			if(a.getAnnotation(Js.class) != null){
				if(Modifier.isStatic(memA.getModifiers())){
					throw new RuntimeException(a+ " is static and hence can not be annotated with @Js");
				}
				annotated.add(a);
			}
			Class<?> clazzA = memA.getDeclaringClass();
			if(!result.containsKey(clazzA)){
				result.put(clazzA, ClassFile.parse(clazzA));
			}
			Body b = result.get(clazzA).get(a);
			if(b != null)
			for(Instruction i : b.body){
				for(Identifier id : i.getParameter()){
					if(id.accessible != null)translate.add(id.accessible);
				}
			}
		}
	}
	
	protected abstract AccessibleObject replacement(AccessibleObject accessible);
	protected abstract Class<?> replacement(Class<?> clazz);
	
	/*
	 * Writer
	 */
	
	public void writeObjects(PrintWriter out){
		for(Class<?> clazz : result.keySet()){
			String cname = getName(clazz);
			out.print("function ");
			out.print(cname);
			out.println("(){}");
		}
		for(Class<?> clazz : result.keySet()){
			JsWriter writer = this;
			try {
				if(JsWriter.class.isAssignableFrom(clazz)){
					writer = (JsWriter) clazz.newInstance();
				}
			} catch (InstantiationException e) {
				//Intended fall through
			} catch (IllegalAccessException e) {
				//Intended fall through
			}
			writeObject(out, clazz, writer);
		}
	}
	
	private void writeObject(PrintWriter out, Class<?> clazz, JsWriter writer){
		Class<?> s = replacement(clazz.getSuperclass());
		String sname = "Object";
		if(result.containsKey(s))sname = getName(s);
		out.print(getName(clazz));
		out.print(".prototype=new ");
		out.print(sname);
		out.println("();");
		
		//TODO add parameter _1
		HashMap<AccessibleObject, Body> accessibles = result.get(clazz);
		for(AccessibleObject accessible : accessibles.keySet()){
			out.print(getMethodName(accessible,true));
			out.print("=function(");
			out.println("){");
			this.writeAccessible(out, accessible, accessibles.get(accessible));
			out.println("};");
		}
	}
	
	@Override
	public void writeInvocation(PrintWriter out, JsWriterModule defaultWriter,  AccessibleObject accessible, AccessibleObject called, Identifier... parameter) {
		//TODO
	}
	
	/*
	 * Naming
	 */
	
	private final HashMap<AnnotatedElement, String> names = new HashMap<AnnotatedElement, String>();
	private final HashMap<AccessibleObject, HashMap<Identifier, String>> locals = new HashMap<AccessibleObject, HashMap<Identifier,String>>();
	
	protected String getMethodName(AccessibleObject accessible, boolean prototype){
		if(!(accessible instanceof Member))throw new RuntimeException(accessible+" should be Member");
		Class<?> clazz = ((Member)accessible).getDeclaringClass();
		return getName(clazz) + (prototype?".prototype.":".") + getName(accessible);
	}
	
	public String getName(AnnotatedElement annotated){
		if(names.containsKey(annotated))return names.get(annotated);
		Js a = annotated.getAnnotation(Js.class);
		String newName;
		if(a == null  || a.name().length() == 0){
			newName = toIdentifier(names.size(), annotated instanceof Class<?>);
		}else{
			newName = a.name();
		}
		names.put(annotated, newName);
		return newName;
	}
	
	public String getLocal(AccessibleObject parent, Identifier id){
		if(!locals.containsKey(parent))locals.put(parent, new HashMap<Identifier, String>());
		HashMap<Identifier, String> map = locals.get(parent);
		if(map.containsKey(id))return map.get(id);
		String newName = toIdentifier(map.size(), false);
		map.put(id, newName);
		return newName;
	}
	
	private String toIdentifier(int id, boolean upper){
		int first = id % 26;
		int rest = id / 26;
		String srest = new StringBuffer(Integer.toString(rest, 36)).reverse().toString();
		return (char)((upper? 'A' : 'a')+first) + (rest > 0 ? srest : "");
	}
}
