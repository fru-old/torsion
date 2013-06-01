package com.github.fru.torsion.bytecode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ClassFileConstant {

	public static enum ClassFileConstantType {
		String, Class, NameAndType, MethodRef, FieldRef, InterfaceMethodRef, Integer, Float, Long, Double, Utf8;
	}

	Object value = null;

	int ref1 = -1;

	int ref2 = -1;

	private ClassFileConstantType type;
	
	public String getConstant(){
		return ""+value;
	}
	
	public Object getValue(){
		return value;
	}
	
	public int getRef1(){
		return ref1;
	}
	
	public int getRef2(){
		return ref2;
	}

	private ClassFileConstant(Object value) {
		this.value = value;
	}

	private ClassFileConstant(int ref1) {
		this.ref1 = ref1;
	}

	private ClassFileConstant(int ref1, int ref2) {
		this.ref1 = ref1;
		this.ref2 = ref2;
	}

	public ClassFileConstantType getType() {
		return type;
	}

	public void setType(ClassFileConstantType type) {
		this.type = type;
	}

	public String toString() {
		String out = "";
		if (type != null) out += type + ": ";
		if (value != null) {
			out += value;
		} else if (ref2 != -1) {
			out += ref1 + " - " + ref2;
		} else {
			out += ref1;
		}
		return out;
	}
	
	public String toString(Map<Integer, ClassFileConstant> constants){
		String out = "";
		if (type != null) out += type + ": ";
		if (value != null) {
			out += value;
		} else if (ref2 != -1) {
			out += constants.get(ref1).toString(constants) + " - " + constants.get(ref2).toString(constants);
		} else {
			out += constants.get(ref1).toString(constants);
		}
		return out;
	}

	public static ClassFileConstant parse(ByteInputStream reader) throws IOException {
		int byteType = reader.findNext();
		ClassFileConstant.ClassFileConstantType type = null;
		ClassFileConstant constant = null;
		switch (byteType) {
		case 1: // UTF-8
			type = ClassFileConstant.ClassFileConstantType.Utf8;
			byte[] bytes = new byte[reader.findShort()];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) reader.findNext();
			}
			try {
				constant = new ClassFileConstant(new String(bytes, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 3: // Integer
			type = ClassFileConstant.ClassFileConstantType.Integer;
			constant = new ClassFileConstant(reader.findInt());
			break;
		case 4: // Float
			type = ClassFileConstant.ClassFileConstantType.Float;
			constant = new ClassFileConstant(Float.intBitsToFloat(reader.findInt()));
			break;
		case 5: // Long
			type = ClassFileConstant.ClassFileConstantType.Long;
			constant = new ClassFileConstant(reader.findLong());
			break;
		case 6: // Double
			type = ClassFileConstant.ClassFileConstantType.Double;
			constant = new ClassFileConstant(Double.longBitsToDouble(reader.findLong()));
			break;
		case 9: // FieldRef
			type = ClassFileConstant.ClassFileConstantType.FieldRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 10: // MethodRef
			type = ClassFileConstant.ClassFileConstantType.MethodRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 11: // InterfaceMethodRef
			type = ClassFileConstant.ClassFileConstantType.InterfaceMethodRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 12: // NameAndType
			type = ClassFileConstant.ClassFileConstantType.NameAndType;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 7: // Class
			type = ClassFileConstant.ClassFileConstantType.Class;
			constant = new ClassFileConstant(reader.findShort());
			break;
		case 8: // String
			type = ClassFileConstant.ClassFileConstantType.String;
			constant = new ClassFileConstant(reader.findShort());
			break;
		default:
			String message = "Found [" + byteType + "] but expected any of [01,03,04,05,06,07,08,09,0A,0B,0C] at Position "
					+ reader.getPosition();
			throw new IOException(message);
		}
		constant.setType(type);
		return constant;
	}
}
