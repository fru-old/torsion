package com.github.fru.torsion.example;

import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;

public class Buildin {

	@Js
	public static class Object{
		
		@JsNative(inline = "")
		public Object(){
			
		}
	}
	
	@Js
	public static abstract class Number{
		@JsNative(inline = "@0 = new Number(@2);")
		public abstract Integer valueOf(int i);
	}
	
	@Js
	public static abstract class Boolean{
		@JsNative(inline = "@0 = new Boolean(@2);")
		public abstract Boolean valueOf(boolean i);
	}
	
	@Js
	public static abstract class String{
		@JsNative(inline = "@0 = new String(@2);")
		public abstract String valueOf(int i);
		
		@JsNative(inline = "@0 = @2 + @3;")
		public abstract String append(java.lang.String s);
		
		@JsNative(inline = "@0 = @2;")
		public abstract java.lang.String toString();
		
		@JsNative(inline = "@1 = @2;")
		public String(java.lang.String s){
			
		}
	}
	
}
