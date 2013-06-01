package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public class PrimitiveOperation extends Body.AbstractParser{
	
	public PrimitiveOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}
	
	
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		String operation = null;
		Class<?> type = null;
		
		if(0x60 <= bytecode && bytecode <= 0x77){
			String[] operations = {"+","-","*","/","%","negate"};
			operation = operations[(bytecode-0x60)/4];
			type = PrimitiveOperation.getBasicType((bytecode-0x60)%4);
			
		}else if(0x78 <= bytecode && bytecode <= 0x83){
			String[] operations = {"shl","shr","ushr","and","or","xor"};
			operation = operations[(bytecode-0x78)/2];
			type = PrimitiveOperation.getBasicType((bytecode-0x78)%2);	
		}else if(bytecode == 0x84){
			int local = byteStream.findNext();
			byte value = (byte)byteStream.findNext();
			Instruction i = new Instruction(location,"+");
			Identifier l = new Identifier(new Identifier.LocalVariable(local));
			Identifier c = new Identifier();
			c.type.con(value);
			i.add(l).add(c).add(l);
			body.add(i);
			return;
		}else if(0x94 <= bytecode && bytecode <= 0x98){
			Instruction i = new Instruction(location,"compare");
			Identifier r = new Identifier();
			r.type.add(int.class);
			i.add(r).add(stack.pop()).add(stack.pop());
			stack.push(r);
			body.add(i);
			return;
		}
		
		Identifier to = new Identifier();
		to.type.add(type);
		
		Instruction i;
		if(0x74 <= bytecode && bytecode <= 0x77){//Negation
			Identifier from = stack.pop();
			i = new Instruction(location,operation).add(to).add(from);
		}else{
			Identifier from1 = stack.pop();
			Identifier from2 = stack.pop();
			i = new Instruction(location,operation).add(to).add(from1).add(from2);
		}
		stack.push(to);
		body.add(i);
	}
	
	private static Class<?> getBasicType(int i) {
		return new Class<?>[]{int.class,long.class,float.class,
				double.class,null,byte.class,char.class,short.class}[i];
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x60 <= bytecode && bytecode <= 0x84 || 0x94 <= bytecode && bytecode <= 0x98;
	}
}
