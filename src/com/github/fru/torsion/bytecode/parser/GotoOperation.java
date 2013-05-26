package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.MethodBody;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class GotoOperation extends MethodBody.AbstractParser{
	
	public GotoOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body) {
		super(stack,constants,body);
	}

	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws IOException{
		String op = null;
		Variable<?> mid = null;
		Variable<?> comp = null;
		Variable<?> location = null;
		
		switch(bytecode){
			case 0x99:
			case 0x9A:
			case 0x9B:
			case 0x9C:
			case 0x9D:
			case 0x9E:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x99];
				comp = new Variable<Integer>(0,Type.INTEGER);
				mid = new Variable.Default(Type.BOOLEAN);
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findShort()),Type.LOCATION);
				break;
			case 0x9F:
			case 0xA0:
			case 0xA1:
			case 0xA2:
			case 0xA3:
			case 0xA4:
				op = new String[] { "==", "!=", "<", ">=", ">", "<=" }[bytecode - 0x9F];
				comp = stack.pop();
				mid = new Variable.Default(Type.BOOLEAN);
				
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findShort()),Type.LOCATION);
				break;
			case 0xA5:
			case 0xA6:
				op = new String[] { "==", "!=" }[bytecode - 0xA5];
				comp = stack.pop();
				mid = new Variable.Default(Type.BOOLEAN);
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findShort()),Type.LOCATION);
				break;
			case 0xA7:
				mid = new Variable<Boolean>(true,Type.BOOLEAN);
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findShort()),Type.LOCATION);
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
				mid = new Variable<Boolean>(true,Type.BOOLEAN);
				location = null;
				break;
			case 0xB1:
				mid = new Variable<Boolean>(true,Type.BOOLEAN);
				location = null;
				break;
			case 0xC6:
			case 0xC7:
				op = new String[] { "==", "!=" }[bytecode - 0xC7];
				comp = new Variable<Object>(null, Type.NULL);
				mid = new Variable.Default(Type.BOOLEAN);
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findShort()),Type.LOCATION);
				break;
			case 0xC8:
				mid = new Variable<Boolean>(true,Type.BOOLEAN);
				location = new Variable<Long>((long)(byteStream.getByteCount() + (short) byteStream.findInt()),Type.LOCATION);
			case 0xC9:
				throw new IOException("subroutines are not supported.");
		}
			
		if(op != null)out.add(new Instruction(op).add(stack.pop()).add(comp).add(mid));
			
		if(location == null){
			out.add(new Instruction("<return>").add(mid).add(stack.pop()));
		}else{
			out.add(new Instruction(Instruction.GOTO_INSTRUCTION).add(mid).add(location));
		}
	}
	
	
	public boolean isApplicable(int bytecode){
		if(0x99 <= bytecode && bytecode <= 0xB1)return true;
		if(0xC6 <= bytecode && bytecode <= 0xC9)return true;
		return false;
	}
	
}
