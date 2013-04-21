package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class ConversionBytecodeParser extends BytecodeParser{
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException {
		Type fromType = null;
		Type toType = null;
		
		switch(bytecode){
		case 0x85:
			fromType = Type.INTEGER_TYPE;
			toType = Type.LONG_TYPE;
			break;
		case 0x86:
			fromType = Type.INTEGER_TYPE;
			toType = Type.FLOAT_TYPE;
			break;
		case 0x87:
			fromType = Type.INTEGER_TYPE;
			toType = Type.DOUBLE_TYPE;
			break;
		case 0x88:
			fromType = Type.LONG_TYPE;
			toType = Type.INTEGER_TYPE;
			break;
		case 0x89:
			fromType = Type.LONG_TYPE;
			toType = Type.FLOAT_TYPE;
			break;
		case 0x8A:
			fromType = Type.LONG_TYPE;
			toType = Type.DOUBLE_TYPE;
			break;
		case 0x8B:
			fromType = Type.FLOAT_TYPE;
			toType = Type.INTEGER_TYPE;
			break;
		case 0x8C:
			fromType = Type.FLOAT_TYPE;
			toType = Type.LONG_TYPE;
			break;
		case 0x8D:
			fromType = Type.FLOAT_TYPE;
			toType = Type.DOUBLE_TYPE;
			break;
		case 0x8E:
			fromType = Type.DOUBLE_TYPE;
			toType = Type.INTEGER_TYPE;
			break;
		case 0x8F:
			fromType = Type.DOUBLE_TYPE;
			toType = Type.LONG_TYPE;
			break;
		case 0x90:
			fromType = Type.DOUBLE_TYPE;
			toType = Type.FLOAT_TYPE;
			break;
			
		//smaller types	
		case 0x91:
			fromType = Type.INTEGER_TYPE;
			toType = Type.BYTE_TYPE;
			break;
		case 0x92:
			fromType = Type.INTEGER_TYPE;
			toType = Type.CHAR_TYPE;
			break;
		case 0x93:
			fromType = Type.INTEGER_TYPE;
			toType = Type.SHORT_TYPE;
			break;
		}
		
		Instruction i = new Instruction("()",toType,Variable.STACK,Variable.STACK);
		i.setType(toType, fromType);
		out.add(i);
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x85 <= bytecode && bytecode <= 0x93;
	}
}
