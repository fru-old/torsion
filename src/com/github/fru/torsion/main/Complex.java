package com.github.fru.torsion.main;

import com.github.fru.torsion.example.Action;
import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js(global=true)
public abstract class Complex {

	@Js(action="load")
	public void first(){
		String i = inc(13)+"";
		
		alert("test"+i);
		new Action.Arg0("func").call();
	}
	
	@Js(name="func")
	protected void func(){
		alert("1233");
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
	
	public int inc(int x){
		return x + 1;
	}
}
