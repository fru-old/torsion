package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.Body;

public class GotoOperation extends Body.AbstractParser{
	
	public GotoOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws IOException {
		String op = null;
		Identifier mid = new Identifier();
		mid.type.add(boolean.class);
		Identifier comp = null;
		Integer index = null;
		
		switch(bytecode){
			case 0x99:
			case 0x9A:
			case 0x9B:
			case 0x9C:
			case 0x9D:
			case 0x9E:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x99];
				comp = new Identifier();
				comp.type.con(new Integer(0));
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
				break;
			case 0x9F:
			case 0xA0:
			case 0xA1:
			case 0xA2:
			case 0xA3:
			case 0xA4:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x9F];
				comp = stack.pop();
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
				break;
			case 0xA5:
			case 0xA6:
				op = new String[] { "==", "!=" }[bytecode - 0xA5];
				comp = stack.pop();
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
				break;
			case 0xA7:
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
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
			case 0xB1:
				Instruction r = new Instruction(location,".return");
				if(bytecode != 0xB1){
					r.add(stack.pop());
				}
				body.add(r);
				return;
			case 0xC6:
			case 0xC7:
				op = new String[] { "==", "!=" }[bytecode - 0xC7];
				comp = new Identifier();
				comp.type.con(null);
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
				break;
			case 0xC8:
				index =  byteStream.getByteCount() + (short) byteStream.findShort();
				break;
			case 0xC9:
				throw new IOException("subroutines are not supported.");
		}
			
		if(op != null){
			body.add(new Instruction(location,op).add(mid).add(stack.pop()).add(comp));
		}
		stack.push(mid);
		body.add(new Instruction.Jump(location, index).add(mid));
	}
	
	
	public boolean isApplicable(int bytecode){
		if(0x99 <= bytecode && bytecode <= 0xB1)return true;
		if(0xC6 <= bytecode && bytecode <= 0xC9)return true;
		return false;
	}
	
}
