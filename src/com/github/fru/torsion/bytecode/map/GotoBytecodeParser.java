package com.github.fru.torsion.bytecode.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Variable;

public class GotoBytecodeParser extends BytecodeParser {

	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable> stack) throws IOException{
		String op = null;
		Variable mid = null;
		Variable comp = null;
		Variable location = null;
		
		switch(bytecode){
			case 0x99:
			case 0x9A:
			case 0x9B:
			case 0x9C:
			case 0x9D:
			case 0x9E:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x99];
				comp = new Variable(0);
				mid = new Variable();
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findShort()));
				break;
			case 0x9F:
			case 0xA0:
			case 0xA1:
			case 0xA2:
			case 0xA3:
			case 0xA4:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x9F];
				comp = stack.pop();
				mid = new Variable();
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findShort()));
				break;
			case 0xA5:
			case 0xA6:
				op = new String[] { "==", "!=" }[bytecode - 0xA5];
				comp = stack.pop();
				mid = new Variable();
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findShort()));
				break;
			case 0xA7:
				mid = new Variable("true");
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findShort()));
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
				mid = new Variable("true");
				location = null;
				break;
			case 0xB1:
				mid = new Variable("true");
				location = null;
				break;
			case 0xC6:
			case 0xC7:
				op = new String[] { "==", "!=" }[bytecode - 0xC7];
				comp = new Variable("null");
				mid = new Variable();
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findShort()));
				break;
			case 0xC8:
				mid = new Variable("true");
				location = new Variable((long)(byteStream.getByteCount() + (short) byteStream.findInt()));
			case 0xC9:
				throw new IOException("subroutines are not supported.");
		}
			
		if(op != null)out.add(new Instruction(op, stack.pop(), comp, mid));
			
		if(location == null){
			out.add(new Instruction("<return>", mid, stack.push(new Variable())));
		}else{
			out.add(new Instruction(Instruction.GOTO_INSTRUCTION, mid, location));
		}
		
		//TODO: Add type information
	}
	
	
	public boolean isApplicable(int bytecode){
		if(0x99 <= bytecode && bytecode <= 0xB1)return true;
		if(0xC6 <= bytecode && bytecode <= 0xC9)return true;
		return false;
	}
	
}
