package com.github.fru.torsion.bytecode.utils;

import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.ClassFileConstantType;

public abstract class Type {
	
	public static class TorsionTypeException extends RuntimeException{
		private static final long serialVersionUID = 1L;
		
		public TorsionTypeException(){
			super("");
		}
	}

	public static final Type VOID = new Basic("V");
	public static final Type INTEGER = new Basic("I");
	public static final Type LONG = new Basic("J");
	public static final Type FLOAT = new Basic("D");
	public static final Type DOUBLE = new Basic("F");
	public static final Type BYTE = new Basic("B");
	public static final Type CHAR = new Basic("C");
	public static final Type SHORT = new Basic("S");
	public static final Type BOOLEAN = new Basic("Z");
	public static final Type NULL = new ObjectType(Object.class);
	public static final Type STRING = new ObjectType(String.class);
	public static final Type TYPE = new Internal("<Type>");
	public static final Type LOCATION = new Internal("<Location>");
	public static final Type REFERENCE = new Internal("<Reference>");
	
	public boolean isAssignableFrom(Type sub) {
		if(sub == null)return false;
		return this.getClass().equals(sub.getClass());
	}
	
	public static Type parseType(String string){
		if(string.equals("byte") || string.equals("B")){
			return Type.BYTE;
		}else if(string.equals("char") || string.equals("C")){
			return Type.CHAR;
		}else if(string.equals("boolean") || string.equals("Z")){
			return Type.BOOLEAN;
		}else if(string.equals("short") || string.equals("S")){
			return Type.SHORT;
		}else if(string.equals("int") || string.equals("I")){
			return Type.INTEGER;
		}else if(string.equals("long") || string.equals("J")){
			return Type.LONG;
		}else if(string.equals("double") || string.equals("D")){
			return Type.DOUBLE;
		}else if(string.equals("float") || string.equals("F")){
			return Type.FLOAT;
		}else if(string.startsWith("[")){
			Type inner = parseType(string.substring(1));
			return new Array(inner);
		}else{
			try {
				if(string.charAt(0) == 'L')string = string.substring(1);
				if(string.charAt(string.length()-1) == ';')string = string.substring(0,string.length()-1);
				final Class<?> type = Class.forName(string);
				return new ObjectType(type);	
			} catch (Exception e) {
				return null;
			} 
		}
	}
	
	public static Type reduceType(Type type){
		if(type == BYTE || type == CHAR || type == SHORT)return INTEGER;
		return type;
	}
	
	private static final Type[] basicType = {INTEGER, LONG, FLOAT, DOUBLE, NULL, BYTE, CHAR, SHORT};
	
	public static Type getBasicType(int order){
		return basicType[order];
	}
	
	public static Type getConstantType(int index, Map<Integer, ClassFileConstant> constants){
		ClassFileConstant constant = constants.get(index);
		if(constant.getType() == ClassFileConstantType.String)return Type.STRING;
		if(constant.getType() == ClassFileConstantType.Integer)return Type.INTEGER;	
		if(constant.getType() == ClassFileConstantType.Float)return Type.FLOAT;	
		if(constant.getType() == ClassFileConstantType.Double)return Type.DOUBLE;	
		if(constant.getType() == ClassFileConstantType.Long)return Type.LONG;	
		return null;
	}
	
	public static class ObjectType extends Type{
		public final Class<?> clazz;
		
		public ObjectType(Class<?> clazz){
			this.clazz = clazz;
		}
		
		public String toString(){
			return "L"+clazz.getName()+";";
		}
		
		public boolean isAssignableFrom(Type sub) {
			return super.isAssignableFrom(sub) && ((ObjectType)sub).clazz.equals(this.clazz);
		}
		
		public void expand(Class<?> clazz){
			//TODO java common superclass
		}
	}
	
	public static class Basic extends Type{
		public final String name;
		
		public Basic(String name){
			this.name = name;
		}
		
		public String toString(){
			return name;
		}
		
		public boolean isAssignableFrom(Type sub) {
			return super.isAssignableFrom(sub) && ((Basic)sub).name.equals(this.name);
		}
	}
	
	public static class Internal extends Type{
		public final String name;
		
		public Internal(String name){
			this.name = name;
		}
		
		public String toString(){
			return name;
		}
		
		public boolean isAssignableFrom(Type sub) {
			return super.isAssignableFrom(sub) && ((Internal)sub).name.equals(this.name);
		}
	}
	
	public static class Constant extends Type{
		public final Type type;
		
		public Constant(Type type) {
			this.type = type;
		}		
		
		public String toString(){
			return "CONST_"+type.toString();
		}
		
		public boolean isAssignableFrom(Type sub) {
			return super.isAssignableFrom(sub) && ((Constant)sub).type.equals(this.type);
		}
	}
	
	public static class Array extends Type{
		public final Type type;
		
		public Array(Type type){
			this.type = type;
		}
		
		public String toString(){
			return "["+type.toString();
		}
		
		public boolean isAssignableFrom(Type sub) {
			return super.isAssignableFrom(sub) && ((Array)sub).type.equals(this.type);
		}
	}
}
