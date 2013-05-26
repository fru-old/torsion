package com.github.fru.torsion.writer;

import java.io.PrintWriter;

import com.github.fru.torsion.writer.normalization.Method;
import com.github.fru.torsion.writer.normalization.Structure;

public class JavascriptWriter extends AbstractWriter{

	final String namespace;
	
	public JavascriptWriter(PrintWriter writer, String namespace){
		super(writer);
		this.namespace = namespace;
		begin();
	}
	
	private void begin(){
		this.writer.append("(function(){");
		this.writer.append("window."+namespace+"={};");
	}
	
	@Override
	public void close(){
		this.writer.append("})();");
	}
	
	@Override
	public void addType(Structure context){
		this.writer.append(this.namespace+"."+context.name+"=function(){};");
		if(context.parent!=null)
			this.writer.append(this.namespace+"."+context.name+".prototype="+context.parent+";");
	}
	
	@Override
	public void addGlobal(Method context){
		
	}
	
	@Override
	public void addMethod(Method context){
		
	}
}