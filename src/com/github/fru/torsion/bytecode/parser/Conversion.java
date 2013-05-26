package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class Conversion extends Abstract{
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws EOFException {
		@SuppressWarnings("unused")
		Type fromType = null;
		Type toType = null;
		
		switch(bytecode){
		case 0x85:
			fromType = Type.INTEGER;
			toType = Type.LONG;
			break;
		case 0x86:
			fromType = Type.INTEGER;
			toType = Type.FLOAT;
			break;
		case 0x87:
			fromType = Type.INTEGER;
			toType = Type.DOUBLE;
			break;
		case 0x88:
			fromType = Type.LONG;
			toType = Type.INTEGER;
			break;
		case 0x89:
			fromType = Type.LONG;
			toType = Type.FLOAT;
			break;
		case 0x8A:
			fromType = Type.LONG;
			toType = Type.DOUBLE;
			break;
		case 0x8B:
			fromType = Type.FLOAT;
			toType = Type.INTEGER;
			break;
		case 0x8C:
			fromType = Type.FLOAT;
			toType = Type.LONG;
			break;
		case 0x8D:
			fromType = Type.FLOAT;
			toType = Type.DOUBLE;
			break;
		case 0x8E:
			fromType = Type.DOUBLE;
			toType = Type.INTEGER;
			break;
		case 0x8F:
			fromType = Type.DOUBLE;
			toType = Type.LONG;
			break;
		case 0x90:
			fromType = Type.DOUBLE;
			toType = Type.FLOAT;
			break;
			
		//smaller types	
		case 0x91:
			fromType = Type.INTEGER;
			toType = Type.BYTE;
			break;
		case 0x92:
			fromType = Type.INTEGER;
			toType = Type.CHAR;
			break;
		case 0x93:
			fromType = Type.INTEGER;
			toType = Type.SHORT;
			break;
		}
		
		Instruction i = new Instruction("()").add(new Variable<Type>(toType,Type.TYPE)).add(stack.pop()).add(stack.push(new Variable.Default(toType)));
		out.add(i);
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x85 <= bytecode && bytecode <= 0x93;
	}
}
