package com.github.fru.torsion.bytecode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;

public class ClassFileConstant {

	public static enum Type {
		String, Class, NameAndType, MethodRef, FieldRef, InterfaceMethodRef, Integer, Float, Long, Double, Utf8;
	}

	String value = null;

	int ref1 = -1;

	int ref2 = -1;

	private Type type;
	
	public String getConstant(){
		return value;
	}

	private ClassFileConstant(String value) {
		this.value = value;
	}

	private ClassFileConstant(int ref1) {
		this.ref1 = ref1;
	}

	private ClassFileConstant(int ref1, int ref2) {
		this.ref1 = ref1;
		this.ref2 = ref2;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
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

	public static ClassFileConstant parse(ByteInputStream reader) throws IOException {
		int byteType = reader.findNext();
		ClassFileConstant.Type type = null;
		ClassFileConstant constant = null;
		switch (byteType) {
		case 1: // UTF-8
			type = ClassFileConstant.Type.Utf8;
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
			type = ClassFileConstant.Type.Integer;
			constant = new ClassFileConstant("" + reader.findInt());
			break;
		case 4: // Float
			type = ClassFileConstant.Type.Float;
			constant = new ClassFileConstant("" + Float.intBitsToFloat(reader.findInt()));
			break;
		case 5: // Long
			type = ClassFileConstant.Type.Long;
			constant = new ClassFileConstant("" + reader.findLong());
			break;
		case 6: // Double
			type = ClassFileConstant.Type.Double;
			constant = new ClassFileConstant("" + Double.longBitsToDouble(reader.findLong()));
			break;
		case 9: // FieldRef
			type = ClassFileConstant.Type.FieldRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 10: // MethodRef
			type = ClassFileConstant.Type.MethodRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 11: // InterfaceMethodRef
			type = ClassFileConstant.Type.InterfaceMethodRef;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 12: // NameAndType
			type = ClassFileConstant.Type.NameAndType;
			constant = new ClassFileConstant(reader.findShort(), reader.findShort());
			break;
		case 7: // Class
			type = ClassFileConstant.Type.Class;
			constant = new ClassFileConstant(reader.findShort());
			break;
		case 8: // String
			type = ClassFileConstant.Type.String;
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
