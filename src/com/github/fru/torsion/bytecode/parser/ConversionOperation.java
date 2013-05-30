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

public class ConversionOperation extends Body.AbstractParser{
	
	public ConversionOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}
	
	
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		@SuppressWarnings("unused")
		Class<?> fromType = null;
		Class<?> toType = null;
		
		switch(bytecode){
		case 0x85:
			fromType = int.class;
			toType = long.class;
			break;
		case 0x86:
			fromType = int.class;
			toType = float.class;
			break;
		case 0x87:
			fromType = int.class;
			toType = double.class;
			break;
		case 0x88:
			fromType = long.class;
			toType = int.class;
			break;
		case 0x89:
			fromType = long.class;
			toType = float.class;
			break;
		case 0x8A:
			fromType = long.class;
			toType = double.class;
			break;
		case 0x8B:
			fromType = float.class;
			toType = int.class;
			break;
		case 0x8C:
			fromType = float.class;
			toType = long.class;
			break;
		case 0x8D:
			fromType = float.class;
			toType = double.class;
			break;
		case 0x8E:
			fromType = double.class;
			toType = int.class;
			break;
		case 0x8F:
			fromType = double.class;
			toType = long.class;
			break;
		case 0x90:
			fromType = double.class;
			toType = float.class;
			break;
			
		//smaller types	
		case 0x91:
			fromType = int.class;
			toType = byte.class;
			break;
		case 0x92:
			fromType = int.class;
			toType = char.class;
			break;
		case 0x93:
			fromType = int.class;
			toType = short.class;
			break;
		}
		
		Identifier from = stack.pop();
		Identifier to = new Identifier();
		to.type.add(toType);
		body.add(new Instruction(location,"cast").add(to).add(from));
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x85 <= bytecode && bytecode <= 0x93;
	}
}
