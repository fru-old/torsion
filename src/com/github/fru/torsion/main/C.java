package com.github.fru.torsion.main;

public class C {
	
	public int a1(){
		int[] t = new int[0];
		return t.length;
	}
	
	public String cast(Object o){
		if(o instanceof String)return (String)o;
		return null;
	}
	
	public void accessab(){
		@SuppressWarnings("unused")
		Object o = C.class;
	}

}
