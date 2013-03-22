package com.github.fru.torsion.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.github.fru.torsion.utils.ByteInputStream;

public class JavaConstant {

	public static enum Type {
		String, Class, NameAndType, MethodRef, FieldRef, InterfaceMethodRef, Integer, Float, Long, Double, Utf8;
	}

	String value = null;

	int ref1 = -1;

	int ref2 = -1;

	private Type type;

	private JavaConstant(String value) {
		this.value = value;
	}

	private JavaConstant(int ref1) {
		this.ref1 = ref1;
	}

	private JavaConstant(int ref1, int ref2) {
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
		if (type != null)
			out += type + ": ";
		if (value != null) {
			out += value;
		} else if (ref2 != -1) {
			out += ref1 + " - " + ref2;
		} else {
			out += ref1;
		}
		return out;
	}

	public static JavaConstant parse(ByteInputStream reader)
			throws IOException {
		int byteType = reader.findNext();
		JavaConstant.Type type = null;
		JavaConstant constant = null;
		switch (byteType) {
		case 1: // UTF-8
			type = JavaConstant.Type.Utf8;
			byte[] bytes = new byte[reader.findShort()];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) reader.findNext();
			}
			try {
				constant = new JavaConstant(new String(bytes, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 3: // Integer
			type = JavaConstant.Type.Integer;
			constant = new JavaConstant("" + reader.findInt());
			break;
		case 4: // Float
			type = JavaConstant.Type.Float;
			constant = new JavaConstant(""
					+ Float.intBitsToFloat(reader.findInt()));
			break;
		case 5: // Long
			type = JavaConstant.Type.Long;
			constant = new JavaConstant("" + reader.findLong());
			break;
		case 6: // Double
			type = JavaConstant.Type.Double;
			constant = new JavaConstant(""
					+ Double.longBitsToDouble(reader.findLong()));
			break;
		case 9: // FieldRef
			type = JavaConstant.Type.FieldRef;
			constant = new JavaConstant(reader.findShort(), reader.findShort());
			break;
		case 10: // MethodRef
			type = JavaConstant.Type.MethodRef;
			constant = new JavaConstant(reader.findShort(), reader.findShort());
			break;
		case 11: // InterfaceMethodRef
			type = JavaConstant.Type.InterfaceMethodRef;
			constant = new JavaConstant(reader.findShort(), reader.findShort());
			break;
		case 12: // NameAndType
			type = JavaConstant.Type.NameAndType;
			constant = new JavaConstant(reader.findShort(), reader.findShort());
			break;
		case 7: // Class
			type = JavaConstant.Type.Class;
			constant = new JavaConstant(reader.findShort());
			break;
		case 8: // String
			type = JavaConstant.Type.String;
			constant = new JavaConstant(reader.findShort());
			break;
		default:
			String message = "Found ["
					+ byteType
					+ "] but expected any of [01,03,04,05,06,07,08,09,0A,0B,0C] at Position "
					+ reader.getPosition();
			throw new IOException(message);
		}
		constant.setType(type);
		return constant;
	}
}
