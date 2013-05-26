package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.MethodBody;

public class InvocationOperation extends MethodBody.AbstractParser{
	
	public InvocationOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		ClassFileConstant constant = constants.get(byteStream.findShort());
		
		//TODO find type
		
		
		switch(bytecode){
		case 0xB2:
			
			//out.add(new Instruction("=static").add(constant).add(stack.push(new Variable())));
			break;
		case 0xB3:
			//
			ClassFileConstant nameAndType = constants.get(constant.getRef2());
			String name = constants.get(nameAndType.getRef1()).getConstant();
			body.add(new Instruction(location).add(new Identifier(name, clazz)));
			//out.add(new Instruction("=static",stack.pop(),constant));
			break;
		case 0xB4:
			//out.add(new Instruction("=field",constant,stack.push(new Variable())));
			break;
		case 0xB5:
			//out.add(new Instruction("=field",stack.pop(),constant));
			break;
		}
		
		switch(bytecode){
		case 0xB6:
			System.out.println("invokevirtual "+constant);
			//out.add(new Instruction("invokevirtual",constant,stack.pop(),stack.push(new Variable())));
			break;
		case 0xB7:
			//out.add(new Instruction("invokespecial",constant,stack.pop(),stack.push(new Variable())));
			break;
		case 0xB8:
			//out.add(new Instruction("invokestatic",constant,stack.push(new Variable())));
			break;
		case 0xB9:
			byteStream.findShort();
			//out.add(new Instruction("invokeinterface",constant,stack.pop(),stack.push(new Variable())));
			break;
		case 0xBA:
			byteStream.findShort();
			System.out.println("invokedynamic " + constant);
			//out.add(new Instruction("invokedynamic").add(constant).add(stack.push(new Variable())));
			break;
		}
		//TODO: Invocation need to parse types, to get operand count
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(0xB2 <= bytecode && bytecode <= 0xBB)return true;
		if(bytecode == 0xBD)return true;
		return false;
	}

}
