package com.github.fru.torsion.bytecode;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public class Bytecode {

	public static ArrayList<Instruction> parse(ByteInputStream byteStream, long offset, Map<Integer, ClassFileConstant> constants)
			throws EOFException {
		int b = byteStream.findNext();
		ArrayList<Instruction> out = new ArrayList<Instruction>();
		out.add(new Instruction(null, ":", offset));

		String cons;
		String op;
		String name;
		Variable mid;
		Variable other;
		int index;
		long location;

		switch (b) {
		case 0x00:
			return out;
		case 0x01:
			cons = "null";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x02:
		case 0x03:
		case 0x04:
		case 0x05:
		case 0x06:
		case 0x07:
		case 0x08:
			cons = b - 0x03 + ""; // integer -1,0,1,2,3,4,5
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x09:
		case 0x0A:
			cons = b - 0x09 + "L";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x0B:
		case 0x0C:
		case 0x0D:
			cons = b - 0x0B + ".0f";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x0E:
		case 0x0F:
			cons = b - 0x0E + ".0";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x10:
			cons = byteStream.findNext() + "";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x11:
			cons = byteStream.findShort() + "";
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x12:
			index = byteStream.findNext();
			cons = "";// TODO: read String int float from constant pool @index
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x13:
			index = byteStream.findShort();
			cons = "";// TODO: read String int float from constant pool @index
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x14:
			index = byteStream.findShort();
			cons = "";// TODO: read double long from constant pool @index
			out.add(new Instruction(Variable.STACK, "=", cons));
			return out;
		case 0x15:
		case 0x16:
		case 0x17:
		case 0x18:
		case 0x19:
			index = byteStream.findNext();
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x1A:
		case 0x1B:
		case 0x1C:
		case 0x1D:
			index = b - 0x1A;
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x1E:
		case 0x1F:
		case 0x20:
		case 0x21:
			index = b - 0x1E;
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x22:
		case 0x23:
		case 0x24:
		case 0x25:
			index = b - 0x22;
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x26:
		case 0x27:
		case 0x28:
		case 0x29:
			index = b - 0x26;
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x2A:
		case 0x2B:
		case 0x2C:
		case 0x2D:
			index = b - 0x2A;
			out.add(new Instruction(Variable.STACK, "=", index));
			return out;
		case 0x2E:
		case 0x2F:
		case 0x30:
		case 0x31:
		case 0x32:
		case 0x33:
		case 0x34:
		case 0x35:
			// Op: [] Array Index
			out.add(new Instruction(Variable.STACK, "Get[]", Variable.STACK, Variable.STACK));
			return out;
		case 0x36:
		case 0x37:
		case 0x38:
		case 0x39:
		case 0x3A:
			index = byteStream.findNext();
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x3B:
		case 0x3C:
		case 0x3D:
		case 0x3E:
			index = b - 0x3B;
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x3F:
		case 0x40:
		case 0x41:
		case 0x42:
			index = b - 0x3F;
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x43:
		case 0x44:
		case 0x45:
		case 0x46:
			index = b - 0x43;
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x47:
		case 0x48:
		case 0x49:
		case 0x4A:
			index = b - 0x47;
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x4B:
		case 0x4C:
		case 0x4D:
		case 0x4E:
			index = b - 0x4B;
			out.add(new Instruction(index, "=", Variable.STACK));
			return out;
		case 0x4F:
		case 0x50:
		case 0x51:
		case 0x52:
		case 0x53:
		case 0x54:
		case 0x55:
		case 0x56:
			// Op: [] Array Index Value
			out.add(new Instruction(null, "Set[]", Variable.STACK, Variable.STACK, Variable.STACK));
			return out;
		case 0x57:
			out.add(new Instruction(null, "=", Variable.STACK));
			return out;
		case 0x58:
			out.add(new Instruction(null, "stack.rem", 0));
			out.add(new Instruction(null, "stack.rem", 1));
			return out;
		case 0x59:
			out.add(new Instruction(null, "stack.dup", 0, 1));
			return out;
		case 0x5A:
			out.add(new Instruction(null, "stack.dup", 0, 2));
			return out;
		case 0x5B:
			out.add(new Instruction(null, "stack.dup", 0, 3));
			return out;
		case 0x5C:
			out.add(new Instruction(null, "stack.dup", 0, 2));
			out.add(new Instruction(null, "stack.dup", 1, 3));
			return out;
		case 0x5D:
			out.add(new Instruction(null, "stack.dup", 0, 3));
			out.add(new Instruction(null, "stack.dup", 1, 4));
			return out;
		case 0x5E:
			out.add(new Instruction(null, "stack.dup", 0, 4));
			out.add(new Instruction(null, "stack.dup", 1, 5));
			return out;
		case 0x5F:
			mid = new Variable();
			other = new Variable();
			out.add(new Instruction(mid, "=", Variable.STACK));
			out.add(new Instruction(other, "=", Variable.STACK));
			out.add(new Instruction(Variable.STACK, "=", mid));
			out.add(new Instruction(Variable.STACK, "=", other));
			return out;
		case 0x60:
		case 0x61:
		case 0x62:
		case 0x63:
			out.add(new Instruction(Variable.STACK, "+", Variable.STACK, Variable.STACK));
			return out;
		case 0x64:
		case 0x65:
		case 0x66:
		case 0x67:
			out.add(new Instruction(Variable.STACK, "-", Variable.STACK, Variable.STACK));
			return out;
		case 0x68:
		case 0x69:
		case 0x6A:
		case 0x6B:
			out.add(new Instruction(Variable.STACK, "*", Variable.STACK, Variable.STACK));
			return out;
		case 0x6C:
		case 0x6D:
		case 0x6E:
		case 0x6F:
			out.add(new Instruction(Variable.STACK, "/", Variable.STACK, Variable.STACK));
			return out;
		case 0x70:
		case 0x71:
		case 0x72:
		case 0x73:
			out.add(new Instruction(Variable.STACK, "%", Variable.STACK, Variable.STACK));
			return out;
		case 0x74:
		case 0x75:
		case 0x76:
		case 0x77:
			out.add(new Instruction(Variable.STACK, "-", Variable.STACK));
			return out;
		case 0x78:
		case 0x79:
			// Op << Value Shift
			out.add(new Instruction(Variable.STACK, "<<", Variable.STACK, Variable.STACK));
			return out;
		case 0x7A:
		case 0x7B:
			out.add(new Instruction(Variable.STACK, ">>", Variable.STACK, Variable.STACK));
			return out;
		case 0x7C:
		case 0x7D:
			// unsigned shift right
			out.add(new Instruction(Variable.STACK, "u>>", Variable.STACK, Variable.STACK));
			return out;
		case 0x7E:
		case 0x7F:
			out.add(new Instruction(Variable.STACK, "&", Variable.STACK, Variable.STACK));
			return out;
		case 0x80:
		case 0x81:
			out.add(new Instruction(Variable.STACK, "|", Variable.STACK, Variable.STACK));
			return out;
		case 0x82:
		case 0x83:
			out.add(new Instruction(Variable.STACK, "xor", Variable.STACK, Variable.STACK));
			return out;
		case 0x84:
			index = byteStream.findNext();
			cons = byteStream.findNext() + "";
			out.add(new Instruction(index, "+", index, cons));
			return out;
		case 0x85:
		case 0x8C:
		case 0x8F:
			// Long
			out.add(new Instruction(Variable.STACK, "cast", "J", Variable.STACK));
			return out;
		case 0x86:
		case 0x89:
		case 0x90:
			// Float
			out.add(new Instruction(Variable.STACK, "cast", "F", Variable.STACK));
			return out;
		case 0x87:
		case 0x8A:
		case 0x8D:
			// Double
			out.add(new Instruction(Variable.STACK, "cast", "D", Variable.STACK));
			return out;
		case 0x88:
		case 0x8B:
		case 0x8E:
			// Int
			out.add(new Instruction(Variable.STACK, "cast", "I", Variable.STACK));
			return out;
		case 0x91:
			// Byte
			out.add(new Instruction(Variable.STACK, "cast", "B", Variable.STACK));
			return out;
		case 0x92:
			// Char
			out.add(new Instruction(Variable.STACK, "cast", "C", Variable.STACK));
			return out;
		case 0x93:
			// Short
			out.add(new Instruction(Variable.STACK, "cast", "S", Variable.STACK));
			return out;
		case 0x94:
		case 0x95:
		case 0x96:
		case 0x97:
		case 0x98:
			// Compare
			out.add(new Instruction(Variable.STACK, "comp", Variable.STACK, Variable.STACK));
			return out;
		case 0x99:
		case 0x9A:
		case 0x9B:
		case 0x9C:
		case 0x9D:
		case 0x9E:
			op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[b - 0x99];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(mid, op, Variable.STACK, 0));
			out.add(new Instruction(null, "goto", mid, location));
			return out;
		case 0x9F:
		case 0xA0:
		case 0xA1:
		case 0xA2:
		case 0xA3:
		case 0xA4:
			op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[b - 0x9F];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(mid, op, Variable.STACK, Variable.STACK));
			out.add(new Instruction(null, "goto", mid, location));
			return out;
		case 0xA5:
		case 0xA6:
			op = new String[] { "==", "!=" }[b - 0xA5];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(mid, op, Variable.STACK, Variable.STACK));
			out.add(new Instruction(null, "goto", mid, location));
			return out;
		case 0xA7:
			cons = "true";
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(null, "goto", cons, location));
			return out;
			// TODO 0xA8 0xA9 0xC9 Subroutine
			// TODO 0xAA 0xAB Table switch
		case 0xAC:
		case 0xAD:
		case 0xAE:
		case 0xAF:
		case 0xB0:
			cons = "true";
			out.add(new Instruction(Variable.RETURN, "=", Variable.STACK));
			out.add(new Instruction(null, "goto", Variable.END));
			return out;
		case 0xB1:
			cons = "true";
			out.add(new Instruction(null, "goto", cons, Variable.END));
			return out;
		case 0xB2:
		case 0xB3:
		case 0xB4:
		case 0xB5:
			// TODO: Lockup field reference
			name = "" + byteStream.findShort();
			// Op: = This Value
			out.add(new Instruction(name, "=", Variable.STACK, Variable.STACK));
			return out;
			// TODO 0xB6 - 0xBA Invoke method
			// TODO 0xBB New
			// TODO 0xBC, 0xBD, 0xC5 New Array Lookup type (also primitive),
			// multi array
		case 0xBE:
			out.add(new Instruction(Variable.STACK, "length()", Variable.STACK));
			return out;
		case 0xBF:
			// TODO: parameter Variable.STACK clears stack
			out.add(new Instruction(Variable.STACK, "throw", Variable.STACK));
			return out;
			// TODO 0xC0 0xC1 cast object, instanceof
			// TODO 0xC2 0xC3 monitorenter monitorexit
			// TODO 0xC4 wide + next opcode !!!!!!!!!!!!!!!!!!!!!!!!!
		case 0xC6:
		case 0xC7:
			op = new String[] { "==", "!=" }[b - 0xA5];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(mid, op, "null", Variable.STACK));
			out.add(new Instruction(null, "goto", mid, location));
			return out;
		case 0xC8:
			cons = "true";
			location = byteStream.getByteCount() + byteStream.findInt();
			out.add(new Instruction(null, "goto", cons, location));
			return out;
		case 0xCA:
			// Breakpoint
			return out;
		}
		// 0x58 - 0x5F TODO stack operations

		/* Error Case */
		return null;
	}
}
