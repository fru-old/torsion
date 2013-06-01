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

public class TypeDependentOperation extends Body.AbstractParser {

	public TypeDependentOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack, constants, body, clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		
		Identifier i,i2,o,o2 = null;
		if((i = stack.pop()).type.canOnlyBeLongOrDouble()){
			switch (bytecode) {
			case 0x59: //pop2
				break;
			case 0x5C: //dup2
				stack.push(i);
				stack.push(i);
				break;
			case 0x5D: //dup2_x1
				o = stack.pop();
				stack.push(i);
				stack.push(o);
				stack.push(i);
				break;
			case 0x5E: //dup2_x2
				o = stack.pop();
				if(!o.type.canOnlyBeLongOrDouble())o2 = stack.pop();
				stack.push(i);
				if(o2 != null)stack.push(o2);
				stack.push(o);
				stack.push(i);
				break;
			}
		}else{
			switch(bytecode){
			case 0x57: //pop
				break;
			case 0x58: //pop2
				stack.pop();
				break;
			case 0x59: //dup
				stack.push(i);
				stack.push(i);
				break;
			case 0x5A: //dup_x1
				o = stack.pop();
				stack.push(i);
				stack.push(o);
				stack.push(i);
				break;
			case 0x5B: //dup_x2
				o = stack.pop();
				if(!o.type.canOnlyBeLongOrDouble())o2 = stack.pop();
				stack.push(i);
				if(o2 != null)stack.push(o2);
				stack.push(o);
				stack.push(i);
				break;
			case 0x5C: //dup2
				i2 = stack.pop();
				stack.push(i2);
				stack.push(i);
				stack.push(i2);
				stack.push(i);
				break;
			case 0x5D: //dup2_x1
				i2 = stack.pop();
				o = stack.pop();
				stack.push(i2);
				stack.push(i);
				stack.push(o);
				stack.push(i2);
				stack.push(i);
				break;
			case 0x5E: //dup2_x2
				i2 = stack.pop();
				o = stack.pop();
				if(!o.type.canOnlyBeLongOrDouble())o2 = stack.pop();
				stack.push(i2);
				stack.push(i);
				if(o2 != null)stack.push(o2);
				stack.push(o);
				stack.push(i2);
				stack.push(i);
				break;
			case 0x5F: //swap
				i2 = stack.pop();
				stack.push(i);
				stack.push(i2);
				break;
			}
		}
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x57 <= bytecode && bytecode <= 0x5F;
	}
}
