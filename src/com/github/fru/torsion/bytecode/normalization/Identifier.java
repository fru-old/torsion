package com.github.fru.torsion.bytecode.normalization;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.main.B;

public class Identifier {
	
	public static AccessibleObject parseMethodConstant(int name, int type, Class<?> clazz, HashMap<Integer, ClassFileConstant> constants){
		try{
			ClassFileConstant n = constants.get(name);
			ClassFileConstant t = constants.get(type);
			return parseMethod(n.getConstant(), clazz, parseMethodSignatureConstant(t.getConstant()));
		}catch(Exception e){
			return null;
		}
	}

	public static AccessibleObject parseMethod(String name, Class<?> clazz, Class<?>[] signature) throws NoSuchMethodException, SecurityException{
		if(name.equals("<init>")){
			try{
				System.out.println(clazz);
				System.out.println(signature);
				return clazz.getConstructor(signature);
			}catch(Exception e){
				throw new RuntimeException("Private classes are not allowed.");
			}
		}else{
			return clazz.getDeclaredMethod(name, signature);
		}
	}
	
	private static Class<?> classForArray(Class<?> clazz, int count){
		for(int j = 0; j < count; j++){
			clazz = Array.newInstance(clazz, 0).getClass();
		}
		return clazz;
	}
	
	private static Class<?>[] parseMethodSignatureConstant(String constant) {
		ArrayList<Class<?>> out = new ArrayList<Class<?>>();
		char first = constant.charAt(0);
		int middle = constant.indexOf(')');
		if (first != '(' || middle < 0 || middle < 1 || middle > constant.length()) {
			throw new RuntimeException("Could not parse parameter");
		}
		try {
			String in = constant.substring(1, middle);
			for (int i = 0; i < in.length(); i++) {
				int arrayCount = 0;
				while(in.charAt(i) == '['){
					i++;
					arrayCount++;
				}
				Class<?> type;
				
				char c = in.charAt(i);
				if (c == 'L') {
					int start = i + 1;
					while (i < in.length() && in.charAt(i) != ';')
						i++;
					int end = i;
					String clazz = in.substring(start, end).replace('/', '.');
					type = Class.forName(clazz);

				}else{
					switch(c){
						case 'V': type = Void.TYPE; break;
						case 'I': type = int.class; break;
						case 'J': type = long.class; break;
						case 'F': type = float.class; break;
						case 'D': type = double.class; break;
						case 'B': type = byte.class; break;
						case 'C': type = char.class; break;
						case 'S': type = short.class; break;
						case 'Z': type = boolean.class; break;
						default: throw new IOException("Wrong format signature: "+in);
					}
				}
				
				type = classForArray(type, arrayCount);
				out.add(type);
			}
			return out.toArray(new Class<?>[out.size()]);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public final AccessibleObject accessible;
	public final Object id;
	public final Type type = new Type();
	
	public Identifier(String name, String clazz, String signature){
		try{
			Class<?> c = Class.forName(clazz.replace('/', '.'));
			this.accessible = parseMethod(name, c, parseMethodSignatureConstant(signature));
			this.id = null;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}	
	}
	
	public Identifier(String name, Class<?> clazz){
		try{
			this.accessible = clazz.getDeclaredField(name);
			this.id = null;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}
	
	public Identifier(Object object){
		this.accessible = null;
		this.id = object;
	}
	
	private static int count = 54953;
	public Identifier(){
		this(count++);
	}

	public static class LocalVariable{
		int local;
		public LocalVariable(int local){
			this.local = local;
		}
		
		@Override
		public String toString(){
			return "local "+local;
		}
		
		@Override
		public boolean equals(Object o){
			if(!(o instanceof LocalVariable))return false;
			LocalVariable other = (LocalVariable)o;
			return this.local == other.local;
		}
		
		@Override
		public int hashCode(){
			return 34543+local;
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Identifier))return false;
		Identifier other = (Identifier)o;
		if(this.accessible != other.accessible)return false;
		if(this.id != null && !this.id.equals(other.id))return false;
		if(this.id == null && other.id != null)return false;
		return true;
	}
	
	@Override
	public int hashCode(){
		if(this.accessible != null)return this.accessible.hashCode();
		if(this.id != null)return this.id.hashCode();
		return super.hashCode();
	}
	
	
	public String toStringAndType(){
		return toString()+type.toString();
	}
	
	@Override
	public String toString(){
		if(this.accessible != null)return this.accessible.toString();
		if(this.id != null){
			if(this.id instanceof Integer){
				return Integer.toHexString((Integer)this.id);
			}
			return this.id.toString();
		}
		return ""+this.hashCode();
	}
	
	//Normalization: Operation StackAssignment

	public static void main(String[] args) throws Exception {
		parseMethodSignatureConstant("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;");
		System.out.println(Class.forName("java.lang.Integer"));
		System.out.println(Class.forName("java.lang.Integer").getMethod("toString", parseMethodSignatureConstant("()Ljava/lang/String;")));
		System.out.println(B.class.getMethod("test", parseMethodSignatureConstant("(Ljava/lang/Object;Ljava/lang/Object;II)Ljava/lang/String;")));
		System.out.println(B.class.getDeclaredField("a"));
	}
}
