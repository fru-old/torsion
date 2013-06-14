package com.github.fru.torsion.javascript;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;

public interface JsWriter {
	
	public void writeAccessible(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, Body body);
	public void writeInvocation(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, AccessibleObject called, Identifier... parameter);
	
}
