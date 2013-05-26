package com.github.fru.torsion.writer.container;

public class Union3<A,B,C> extends Union<Object> {

	public Union3(A a, B b, C c){
		super(a,b,c);
	}
	
	@SuppressWarnings("unchecked")
	public A first(){
		return (A)super.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public B second(){
		return (B)super.get(1);
	}
	
	@SuppressWarnings("unchecked")
	public C third(){
		return (C)super.get(2);
	}
}
