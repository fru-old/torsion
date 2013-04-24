package com.github.fru.torsion.bytecode.utils;

import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.ClassFileConstantType;

public class Type {

	public static final Type VOID_TYPE = new Type("V");
	public static final Type INTEGER_TYPE = new Type("I");
	public static final Type LONG_TYPE = new Type("J");
	public static final Type FLOAT_TYPE = new Type("D");
	public static final Type DOUBLE_TYPE = new Type("F");
	public static final Type BYTE_TYPE = new Type("B");
	public static final Type CHAR_TYPE = new Type("C");
	public static final Type SHORT_TYPE = new Type("S");
	
	//TODO fix type as class like constant
	public static final Type ARRAY_TYPE = new Type("[");
	
	//TODO fix types
	public static final Type NULL_TYPE = new Type("L");
	public static final Type STRING_TYPE = new Type("L");
	
	public static class ConstantType extends Type{
		
		public ConstantType(Type type) {
			super(type.toString());
		}

		public String toString(){
			return "";
		}
		
		public Object parseValue(){
			return null;
		}
		
		public Type getType(){
			return null;
		}
		
	}
	
	public static Type reduceType(Type type){
		if(type == BYTE_TYPE || type == CHAR_TYPE || type == SHORT_TYPE)return INTEGER_TYPE;
		return type;
	}
	
	private static final Type[] typeOrder = {INTEGER_TYPE, LONG_TYPE, FLOAT_TYPE, DOUBLE_TYPE, NULL_TYPE, BYTE_TYPE, CHAR_TYPE, SHORT_TYPE};
	
	public static Type getType(int order){
		return typeOrder[order];
	}
	
	public final String name;
	
	public Type(String name){
		this.name = name;
	}
	
	public static Type getConstantType(int index, Map<Integer, ClassFileConstant> constants){
		ClassFileConstant constant = constants.get(index);
		if(constant.getType() == ClassFileConstantType.String)return Type.STRING_TYPE;
		if(constant.getType() == ClassFileConstantType.Integer)return Type.INTEGER_TYPE;	
		if(constant.getType() == ClassFileConstantType.Float)return Type.FLOAT_TYPE;	
		if(constant.getType() == ClassFileConstantType.Double)return Type.DOUBLE_TYPE;	
		if(constant.getType() == ClassFileConstantType.Long)return Type.LONG_TYPE;	
		return null;
	}
}
