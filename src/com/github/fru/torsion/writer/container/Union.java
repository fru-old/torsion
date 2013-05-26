package com.github.fru.torsion.writer.container;

import java.util.Arrays;

public class Union<A> {

	A[] as;
	
	@SafeVarargs
	public Union(A... a){
		this.as = a;
	}
	
	public A get(int i){
		return as[i];
	}
	
	@Override
	public String toString(){
		StringBuilder out = new StringBuilder("[");
		for(A a : as){
			out.append(a.toString());
			out.append(',');
		}
		if(out.length() > 1)out.setLength(out.length()-1);
		out.append(']');
		return out.toString();
	}
	
	@Override
	public boolean equals(Object object){
		if(object == null || !(object instanceof Union<?>))return false;
		Union<?> other = (Union<?>)object;
		if(other.as.length != this.as.length)return false;
		int index = 0;
		for(Object o : other.as){
			if(!o.equals(as[index]))return false;
			index++;
		}
		return true;
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(as);
	}
}
