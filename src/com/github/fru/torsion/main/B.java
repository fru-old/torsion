package com.github.fru.torsion.main;

public abstract class B {
	
	protected int a;

	@SuppressWarnings("unused")
	public void test(){
		String.valueOf(true);
		this.toString();
		int t = this.a;
	}
	
	public abstract void no();
	
	@Override
	public String toString(){
		new String();
		return "";
	}
	
	public String test(Object first, Object second, int t, int t2){
		String[][] t4 = new String[3][1];
		return t4[0][0];
	}
}
