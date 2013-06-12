package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;

public interface JsWriter {
	
	public void writeAccessible(PrintWriter out, AccessibleObject accessible, Body body);
	public void writeInvocation(PrintWriter out, AccessibleObject called, Identifier... parameter);
	
}
