package com.github.fru.torsion.main;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js
public abstract class D {

	protected int first(){
		int i = 2;
		return 1 + i;
	}
	
	@JsNative(inline = "alert(@1);")
	protected abstract void alert(Object object);
	
	@Js
	public void run(){
		alert(first());
	}
}
