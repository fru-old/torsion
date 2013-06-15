package com.github.fru.torsion.main;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js(global=true)
public abstract class D {

	protected int first(){
		int i = 5;
		return 1 + i;
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
	
	@Js
	public void run(){
		int i = 0;
		while(i++ < 4){
			alert(5+6);
		}
	}
}
