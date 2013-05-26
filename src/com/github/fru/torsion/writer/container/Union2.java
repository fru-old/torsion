package com.github.fru.torsion.writer.container;

public class Union2<A,B> extends Union<Object> {

	public Union2(A a, B b){
		super(a,b);
	}
	
	@SuppressWarnings("unchecked")
	public A first(){
		return (A)super.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public B second(){
		return (B)super.get(1);
	}
}
