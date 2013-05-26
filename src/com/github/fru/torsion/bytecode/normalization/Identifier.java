package com.github.fru.torsion.bytecode.normalization;

import java.lang.reflect.AccessibleObject;
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
			return clazz.getConstructor(signature);
		}else{
			return clazz.getMethod(name, signature);
		}
	}
	
	public static Class<?>[] parseMethodSignatureConstant(String constant) {
		ArrayList<Class<?>> out = new ArrayList<Class<?>>();
		char first = constant.charAt(0);
		int middle = constant.indexOf(')');
		if (first != '(' || middle < 0 || middle < 1 || middle > constant.length()) {
			throw new RuntimeException("Could not parse parameter");
		}
		try {
			String in = constant.substring(1, middle);
			for (int i = 0; i < in.length(); i++) {
				char c = in.charAt(i);
				if (c == 'L') {
					int start = i + 1;
					while (i < in.length() && in.charAt(i) != ';')
						i++;
					int end = i;
					String clazz = in.substring(start, end).replace('/', '.');
					out.add(Class.forName(clazz));

				}else{
					switch(c){
						case 'V': out.add(Void.TYPE); break;
						case 'I': out.add(int.class); break;
						case 'J': out.add(long.class); break;
						case 'F': out.add(float.class); break;
						case 'D': out.add(double.class); break;
						case 'B': out.add(byte.class); break;
						case 'C': out.add(char.class); break;
						case 'S': out.add(short.class); break;
						case 'Z': out.add(boolean.class); break;
					}
				}
			}
			return out.toArray(new Class<?>[out.size()]);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public final AccessibleObject accessible;
	public final Object id;
	
	public Identifier(String name, Class<?> clazz, String signature){
		try{
			this.accessible = parseMethod(name, clazz, parseMethodSignatureConstant(signature));
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
	
	public Identifier(){
		this(new Object());
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Identifier))return false;
		Identifier other = (Identifier)o;
		return this.accessible == other.accessible && this.id == other.id;
	}
	
	@Override
	public int hashCode(){
		if(this.accessible != null)return this.accessible.hashCode();
		if(this.id != null)return this.id.hashCode();
		return this.hashCode();
	}
	
	@Override
	public String toString(){
		if(this.accessible != null)return this.accessible.toString();
		if(this.id != null)return this.id.toString();
		return this.toString();
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
