package com.github.fru.torsion.main;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;


public interface TorsionOut {
	
	public void outBeforePackage(PrintWriter out, Class<?> clazz);
	public void outTypeDefinition(PrintWriter out, Class<?> clazz);
	public void outBodyDefinition(PrintWriter out, AccessibleObject accessable, Body body);
	public void outCallDefinition(PrintWriter out, AccessibleObject accessable,  AccessibleObject called, Identifier... identifier );
	
}