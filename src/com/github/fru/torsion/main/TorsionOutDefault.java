package com.github.fru.torsion.main;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public abstract class TorsionOutDefault implements TorsionOut {
	
	private Class<? extends Annotation> annotation;
	
	public TorsionOutDefault(Class<? extends Annotation> annotation){
		this.annotation = annotation;
	}
	
	public boolean isStartingPoint(AccessibleObject accessible){
		return accessible.getAnnotation(annotation) != null;
	}
	
	public boolean isClassTranslatable(Class<?> clazz){
		return clazz.getAnnotation(annotation) != null;
	}
	
	public String getPackage(Class<?> clazz){
		return null; //default package
	}
	
	public void before(PrintWriter out){
		throw new NotImplementedException();
	}
	public void after(PrintWriter out){
		throw new NotImplementedException();
	}
}
