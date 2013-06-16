package com.github.fru.torsion.main;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js(global=true)
public abstract class D {

	@Js
	public void main(){
		alert(first());
		String s = "test";
		alert(s);
	}
	
	protected int first(){
		int i = 4;
		if(i > 2){
			String s = "z";
			alert(s);
		}
		return 1 + i;
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
}
