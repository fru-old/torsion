package com.github.fru.torsion.writer.normalization;

import java.util.HashMap;

import com.github.fru.torsion.writer.container.FinalMap;


public class Structure{
	
	public Identifier name = null;
	public Identifier parent = null;
	
	public HashMap<Identifier, Identifier> fields = null; 
	
	public Structure(){
	}
	
	public Structure(String name, String parent){
		this.name = new Identifier(name);
		if(parent!=null)this.parent = new Identifier(parent);
		this.fields = new FinalMap<Identifier,Identifier>();
	}
}
