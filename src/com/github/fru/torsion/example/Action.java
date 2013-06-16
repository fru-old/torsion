package com.github.fru.torsion.example;

import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.javascript.Js;
import com.github.fru.torsion.javascript.JsNative;
import com.github.fru.torsion.javascript.JsWriter;
import com.github.fru.torsion.javascript.JsWriterModule;

@Js
public class Action implements JsWriter{
	
	@Js
	public static class Arg0 extends Action{
		@Deprecated //TODO constructor needed? Not user friendly
		public Arg0(){};
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
	
	/*
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
	}*/

	@Override
	@JsNative()
	public void writeAccessible(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, Body body) {
		//empty intentionally
	}
	
	@Override
	@JsNative()
	public void writeInvocation(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, AccessibleObject called,
			Identifier... parameter) {
		if(called instanceof Constructor){
			String s = parameter[2].type.getConstantValue();
			s = s.substring(1, s.length()-1);
			out.print(defaultWriter.getLocal(accessible, parameter[1]));
			out.print("=this.");
			out.print(s);
			out.println(";");
			//TODO this is not properly set for an action
		}else if(called instanceof Method){
			Method m = (Method)called;
			if(m.getName().equals("call")){
				out.print(defaultWriter.getLocal(accessible, parameter[2]));
				out.println("();");
			}
		}
	}

	/*@Override
	public void writeInvocation(PrintWriter out, JsWriterModule defaultWriter, AccessibleObject accessible, AccessibleObject called,
			Identifier... parameter) {
		if(called instanceof Constructor){
			//TODO
			//if(identifier.length > 1 && identifier[1].type.isConstant()){
			Method m = getMethod("constant");
			if(Modifier.isStatic(m.getModifiers())){
				String ret = defaultWriter.getLocal(accessible, parameter[0]);
				String value = defaultWriter.getName(m.getClass())+"."+defaultWriter.getName(m);
				
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
		
	}*/
}
