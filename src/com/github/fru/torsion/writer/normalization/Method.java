package com.github.fru.torsion.writer.normalization;

import com.github.fru.torsion.writer.container.FinalList;
import com.github.fru.torsion.writer.container.FinalMap;


public class Method{

	public final Identifier name;
	public final FinalMap<Identifier, Identifier> fields; 
	public final FinalList<Operation> body;
	
	public Method(Identifier name){
		this.name = name;
		this.fields = new FinalMap<Identifier,Identifier>();
		this.body = new FinalList<Operation>();
	}
	
	public Method(String name){
		this(new Identifier(name));
	}
	
	public Method(Method m, FinalList<Operation> body){
		this.name = m.name;
		this.fields = m.fields;
		this.body = body;
	}
	
	public String toString(){
		return Method.BlockToString(name.toString(), fields, body);
	}
	
	public static String BlockToString(String name, FinalMap<Identifier, Identifier> fields, FinalList<Operation> body){
		StringBuilder out = new StringBuilder();
		out.append(name);
		if(fields != null){
			out.append(" = (");
			//TODO fields
			out.append(")");
		}
		out.append("{\n");
		for(Operation op : body){
			out.append('\t');
			out.append(op.toString().replace("\n", "\n\t"));
			out.append('\n');
		}
		out.append("}");
		return out.toString();
	}
}
