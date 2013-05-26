package com.github.fru.torsion.writer;

import java.io.PrintWriter;

import com.github.fru.torsion.writer.normalization.Method;
import com.github.fru.torsion.writer.normalization.Structure;

public abstract class AbstractWriter {

	public AbstractWriter(PrintWriter writer){
		this.writer = writer;
	}
	
	protected PrintWriter writer;

	public abstract void close();
	public abstract void addMethod(Method context);
	public abstract void addGlobal(Method context);
	public abstract void addType(Structure context);
}
