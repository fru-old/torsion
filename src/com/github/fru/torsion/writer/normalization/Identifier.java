package com.github.fru.torsion.writer.normalization;

public class Identifier {
	
	public final String name;
	public final int version;
	
	public Identifier(String name, int version){
		this.name = name;
		this.version = version;
	}
	
	public Identifier(String name){
		this(name,0);
	}
	
	@Override
	public String toString(){
		if(version == 0)return name;
		return name+"_"+version;
	}
	
	@Override
	public boolean equals(Object object){
		if(object == null || this.getClass() != object.getClass())return false;
		Identifier other = (Identifier)object;
		return this.name.equals(other.name) && this.version == other.version;
	}
	
	@Override 
	public int hashCode(){
		return name.hashCode()*17 + version;
	}
}
