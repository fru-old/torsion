package com.github.fru.torsion.bytecode.map;

import java.io.IOException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public class GotoBytecodeParser extends BytecodeParser {

	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws IOException{
		String op;
		Variable mid;
		long location;
		String cons;
		
		switch(bytecode){
		case 0x99:
		case 0x9A:
		case 0x9B:
		case 0x9C:
		case 0x9D:
		case 0x9E:
			op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x99];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(op, Variable.STACK, 0, mid));
			out.add(new Instruction("goto", mid, location, null));
			break;
		case 0x9F:
		case 0xA0:
		case 0xA1:
		case 0xA2:
		case 0xA3:
		case 0xA4:
			op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x9F];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(op, Variable.STACK, Variable.STACK, mid));
			out.add(new Instruction("goto", mid, location, null));
			break;
		case 0xA5:
		case 0xA6:
			op = new String[] { "==", "!=" }[bytecode - 0xA5];
			mid = new Variable();
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(op, Variable.STACK, Variable.STACK, mid));
			out.add(new Instruction("goto", mid, location, null));
			break;
		case 0xA7:
			cons = "true";
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction("goto", cons, location, null));
			break;
		case 0xA8:
		case 0xA9:
			throw new IOException("subroutines are not supported.");
		case 0xAA:
		case 0xAB:
			throw new IOException("Switch statements are not supported.");
		case 0xAC:
		case 0xAD:
		case 0xAE:
		case 0xAF:
		case 0xB0:
			cons = "true";
			out.add(new Instruction("=", Variable.STACK, Variable.RETURN));
			out.add(new Instruction("goto", Variable.END, null));
			break;
		case 0xB1:
			cons = "true";
			out.add(new Instruction("goto", cons, Variable.END, null));
			break;
		case 0xC6:
		case 0xC7:
			op = new String[] { "==", "!=" }[bytecode - 0xC7];
			mid = new Variable();
			cons = "null";
			location = byteStream.getByteCount() + (short) byteStream.findShort();
			out.add(new Instruction(op, Variable.STACK, cons, mid));
			out.add(new Instruction("goto", mid, location, null));
		case 0xC8:
			cons = "true";
			location = byteStream.getByteCount() + (short) byteStream.findInt();
			out.add(new Instruction("goto", cons, location, null));
		case 0xC9:
			throw new IOException("subroutines are not supported.");
		}
		
		//TODO: Add type information
	}
	
	
	public boolean isApplicable(int bytecode){
		if(0x99 <= bytecode && bytecode <= 0xB1)return true;
		if(0xC6 <= bytecode && bytecode <= 0xC9)return true;
		return false;
	}
	
}
