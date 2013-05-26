package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class Operation extends Abstract {
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws EOFException {
		String operation = null;
		Type type = null;
		
		if(0x60 <= bytecode && bytecode <= 0x77){
			String[] operations = {"+","-","*","/","%","negate"};
			operation = operations[(bytecode-0x60)/4];
			type = Type.getBasicType((bytecode-0x60)%4);
			
		}else if(0x78 <= bytecode && bytecode <= 0x83){
			String[] operations = {"shl","shr","ushr","and","or","xor"};
			operation = operations[(bytecode-0x78)/2];
			type = Type.getBasicType((bytecode-0x78)%2);	
		}
		
		Instruction i;
		if(0x74 <= bytecode && bytecode <= 0x77){//Negation
			Variable<?> inter = stack.pop();
			i = new Instruction(operation).add(inter).add(stack.push(new Variable.Default(inter.getType())));
		}else{
			i = new Instruction(operation).add(stack.pop()).add(stack.pop()).add(stack.push(new Variable.Default(type)));
		}
		out.add(i);
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x60 <= bytecode && bytecode <= 0x83;
	}
}
