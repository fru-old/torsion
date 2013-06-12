package com.github.fru.torsion.example;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.fru.torsion.buildin.JsOutDefault;
import com.github.fru.torsion.buildin.JsNaming;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;

public class Action extends JsOutDefault{
	
	public static class Arg0 extends Action{
		public Arg0(String name){};
		public void call(){}
	}
	
	public static class Arg1<A> extends Action{
		public Arg1(String name){};
		public void call(A a){}
		public Arg0 curry(A a){return null;}
	}
	
	public static class Arg2<A,B> extends Action{
		public Arg2(String name){};
		public void call(A a, B b){}
		public Arg1<B> curry(A a){return null;}
		public Arg0 curry(A a, B b){return null;}
	}
	
	public static class Arg3<A,B,C> extends Action{
		public Arg3(String name){};
		public void call(A a, B b, C c){}
		public Arg2<B,C> curry(A a){return null;}
		public Arg1<C> curry(A a, B b){return null;}
		public Arg0 curry(A a, B b, C c){return null;}
	}
	
	public static class Arg4<A,B,C,D> extends Action{
		public Arg4(String name){};
		public void call(A a, B b, C c, D d){}
		public Arg3<B,C,D> curry(A a){return null;}
		public Arg2<C,D> curry(A a, B b){return null;}
		public Arg1<D> curry(A a, B b, C c){return null;}
		public Arg0 curry(A a, B b, C c, D d){return null;}
	}

	@Override
	public void outCallDefinition(PrintWriter out, AccessibleObject accessable, AccessibleObject called,
			Identifier... identifier) {
		if(called instanceof Constructor){
			//TODO
			//if(identifier.length > 1 && identifier[1].type.isConstant()){
			Method m = getMethod("constant");
			if(Modifier.isStatic(m.getModifiers())){
				String ret = JsNaming.getLocal(accessable, identifier[0]);
				String value = JsNaming.getName(m.getClass())+"."+JsNaming.getName(m);
				
				out.println(ret+"="+value+";");
				return;
			}
			
		}else if(called instanceof Method){
			Method m = (Method)called;
			if(m.getName().equals("curry")){
				//TODO
				return;
			}else if(m.getName().equals("call")){
				//TODO
				return;
			}
		}
		throw new RuntimeException("Action class could not be called correctly.");
	}
	
	@Override
	public void outBodyDefinition(PrintWriter out, AccessibleObject accessable, Body body) {
		//empty intentionally
	}
	
	private Method getMethod(String name){
		try{
			int l = name.lastIndexOf('.');
			String clazz = name.substring(0,l);
			String method = name.substring(l-1);
			for(Method m : Class.forName(clazz).getMethods()){
				if(m.getName().equals(method)){
					return m;
				}
			}
		}catch(Exception e){
			//intentional fall through
		}
		throw new RuntimeException(name + " is not a valid static method.");
	}
}
