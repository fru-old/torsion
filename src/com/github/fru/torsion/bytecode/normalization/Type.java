package com.github.fru.torsion.bytecode.normalization;

import java.util.ArrayList;

public class Type {
	
	ArrayList<Class<?>> classes = null;
	public Type add(Class<?> clazz){
		if(classes == null)classes = new ArrayList<Class<?>>();
		classes.add(clazz);
		return this;
	}
	
	ArrayList<Object> constants = null;
	public Type con(Object constant){
		if(constants == null)constants = new ArrayList<Object>();
		constants.add(constant);
		return this;
	}
	
	ArrayList<Identifier> assignedFromVars = null;
	public Type add(Identifier assignedFromVar){
		if(assignedFromVars == null)assignedFromVars = new ArrayList<Identifier>();
		assignedFromVars.add(assignedFromVar);
		return this;
	}
	
	boolean resolveArrayTypes = false;
	public Type resolveArrayTypes(){
		resolveArrayTypes = true;
		return this;
	}
	
	public String toString(){
		return ""+(classes!=null?classes:"")+(constants!=null?constants:"")+(assignedFromVars!=null?assignedFromVars:"");
	}

	public static Class<?> getBasicType(int i) {
		return new Class<?>[]{int.class,long.class,float.class,
				double.class,null,byte.class,char.class,short.class}[i];
	}
}
