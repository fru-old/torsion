package com.github.fru.torsion.example;

import com.github.fru.torsion.buildin.Js;
import com.github.fru.torsion.buildin.JsNative;
import com.github.fru.torsion.example.Action.Arg3;

@Js(global = true, name = "Loader")
public abstract class Loader {
	
	private static Action.Arg3<String[], Action.Arg0, Integer> loadNativeAction = 
			new Arg3<String[], Action.Arg0, Integer>("DependencyManagment.loadNative");

	private static void loadNative(String[] urls, Action.Arg0 callback, Integer index) {
		if(urls.length > index){
			Object script = Loader.createScript(urls[index]);
			Loader.setOnLoad(script, loadNativeAction.curry(urls, callback, index-1));
			Loader.appendToHead(script);
		}else{
			callback.call();
		}
	}
	
	public static class CounterClosure{
		private int counter = 0;
		public int increment(){
			return (counter += 1);
		}
	}
	
	private static Action.Arg3<CounterClosure, Integer, Action.Arg0> callAfterCountAction = 
			new Arg3<CounterClosure, Integer, Action.Arg0>("DependencyManagment.callAfterCount");
	
	@SuppressWarnings("unused")
	private static void callAfterCount(CounterClosure closure, Integer count, Action.Arg0 callback){
		if(count <= closure.increment())callback.call();
	}
	
	@Js(global = true, name = "load")
	public static void load(String[][] urls, Action.Arg0 callback){
		CounterClosure closure = new CounterClosure();
		for(int i = 0; i < urls.length; i++){
			Loader.loadNative(urls[i], callAfterCountAction.curry(closure, urls[i].length, callback), 0);
		}
	}
	
	@JsNative(inline = {
		"@0 = window.document.createElement('SCRIPT');",
		"@0.type = 'text/javascript';",
		"@0.src = @1;",
	})
	private static Object createScript(String src){
		return null;
	}
	
	
	@JsNative(inline = {
		"window.document.getElementsByTagName('HEAD')[0].appendChild(@1);",
	})
	private static void appendToHead(Object element){
	}
	
	
	@JsNative(inline = {
		"@1.onload = @1.onreadstatechange = function(){",
		"  if (@1.readyState) {",
		"    if (@1.readyState == 'loaded' || @1.readyState == 'complete') {",
		"      @1.onreadystatechange = null;",
		"      @2();",
		"    }",
		"  }else{",
		"    @1.onload = null;",
		"    @2();",
		"  }",
		"};",
	})
	private static void setOnLoad(Object element, Action.Arg0 callback){
	}
}
