package com.github.fru.torsion.example;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

public class Buildin {

	@Js
	public class Object{
		
	}
	
	@Js
	public abstract class Number{
		@JsNative(inline = "@0 = new Number(@2);")
		public abstract Integer valueOf(int i);
	}
	
	@Js
	public abstract class Boolean{
		@JsNative(inline = "@0 = new Boolean(@2);")
		public abstract Boolean valueOf(boolean i);
	}
	
	@Js
	public class String{
		
	}
	
}
