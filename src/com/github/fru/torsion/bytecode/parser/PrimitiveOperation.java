package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Type;

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
			type = Type.getBasicType((bytecode-0x60)%4);
			
		}else if(0x78 <= bytecode && bytecode <= 0x83){
			String[] operations = {"shl","shr","ushr","and","or","xor"};
			operation = operations[(bytecode-0x78)/2];
			type = Type.getBasicType((bytecode-0x78)%2);	
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

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x60 <= bytecode && bytecode <= 0x83;
	}
}
