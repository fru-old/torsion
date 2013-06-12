package com.github.fru.torsion.buildin;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.normalization.Identifier;

public class JsNaming {

	private static final HashMap<AnnotatedElement, String> names = new HashMap<AnnotatedElement, String>();
	private static final HashMap<AccessibleObject, HashMap<Identifier, String>> locals = new HashMap<AccessibleObject, HashMap<Identifier,String>>();
	
	public static String getName(AnnotatedElement annotated){
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
	
	public static String getLocal(AccessibleObject parent, Identifier id){
		if(!locals.containsKey(parent))locals.put(parent, new HashMap<Identifier, String>());
		HashMap<Identifier, String> map = locals.get(parent);
		if(map.containsKey(id))return map.get(id);
		String newName = toIdentifier(map.size(), false);
		map.put(id, newName);
		return newName;
	}
	
	private static String toIdentifier(int id, boolean upper){
		int first = id % 26;
		int rest = id / 26;
		String srest = new StringBuffer(Integer.toString(rest, 36)).reverse().toString();
		return (char)((upper? 'A' : 'a')+first) + (rest > 0 ? srest : "");
	}
}
