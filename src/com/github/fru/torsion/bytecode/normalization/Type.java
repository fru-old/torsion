package com.github.fru.torsion.bytecode.normalization;

import java.util.ArrayList;

public class Type {
	
	ArrayList<Class<?>> classes = null;
	public Type add(Class<?> clazz){
		if(classes == null)classes = new ArrayList<Class<?>>();
		classes.add(clazz);
		return this;
	}
	
	public ArrayList<Class<?>> getClasses(){
		return classes;
	}
	
	ArrayList<Object> constants = null;
	public Type con(Object constant){
		if(constant instanceof String)constant = "\""+constant+"\"";
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
	
	public boolean isConstant(){
		return constants != null && constants.size() > 0;
	}
	
	public String getConstantValue(){
		if(constants.get(0) == null)return "null";
		return constants.get(0).toString();
	}
	
	public String toString(){
		return ""+(classes!=null?classes:"")+(constants!=null?constants:"")+(assignedFromVars!=null?assignedFromVars:"");
	}
	
	public boolean canOnlyBeLongOrDouble(){
		if(classes!=null)for(Class<?> c : classes){
			if(c == long.class || c == double.class)return true;
		}
		if(constants!=null)for(Object o : constants){
			if(o.getClass() == Long.class || o.getClass() == Double.class )return true;
		}
		return false;
	}
}
