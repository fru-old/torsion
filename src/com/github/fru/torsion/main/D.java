package com.github.fru.torsion.main;

import com.github.fru.torsion.example.Action;
import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

@Js(global=true)
public abstract class D {

	/*@Js(action="direct")
	public void main(){
		boolean allways = true;
		if(allways){
			alert(first() + "abcde");
		}
	}*/
	
	@Js(action="load")
	public void main2(){
		//append("div", "test2");
		//append("div", "test");
		Action.Arg0 a = new Action.Arg0("func");
		a.call();
		int i = inc(234);
		alert(i);
	}
	
	@Js(name="func")
	protected void func(){
		alert("1233");
		
	}
	
	public int inc(int x){
		return x + 1;
	}
	
	@JsNative(inline = "alert(@3);")
	protected abstract void alert(Object object);
	/*
	protected int first(){
		int i = 4;
		while(i < 1634){
			i++;
		}
		return 1 + i;
	}
	
	
	
	@JsNative(inline = {
		"var @1 = window.document.createElement(@3);",
		"@1.innerHTML=@4;",
		"window.document.getElementsByTagName('BODY')[0].appendChild(@1);"	
	})
	protected abstract void append(String tag, String content);*/
}
