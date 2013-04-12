package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public class ConversionBytecodeParser implements BytecodeParser{
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException {
		String fromType = null;
		String toType = null;
		
		switch(bytecode){
		case 0x85:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.LONG_TYPE;
			break;
		case 0x86:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.FLOAT_TYPE;
			break;
		case 0x87:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.DOUBLE_TYPE;
			break;
		case 0x88:
			fromType = Instruction.LONG_TYPE;
			toType = Instruction.INTEGER_TYPE;
			break;
		case 0x89:
			fromType = Instruction.LONG_TYPE;
			toType = Instruction.FLOAT_TYPE;
			break;
		case 0x8A:
			fromType = Instruction.LONG_TYPE;
			toType = Instruction.DOUBLE_TYPE;
			break;
		case 0x8B:
			fromType = Instruction.FLOAT_TYPE;
			toType = Instruction.INTEGER_TYPE;
			break;
		case 0x8C:
			fromType = Instruction.FLOAT_TYPE;
			toType = Instruction.LONG_TYPE;
			break;
		case 0x8D:
			fromType = Instruction.FLOAT_TYPE;
			toType = Instruction.DOUBLE_TYPE;
			break;
		case 0x8E:
			fromType = Instruction.DOUBLE_TYPE;
			toType = Instruction.INTEGER_TYPE;
			break;
		case 0x8F:
			fromType = Instruction.DOUBLE_TYPE;
			toType = Instruction.LONG_TYPE;
			break;
		case 0x90:
			fromType = Instruction.DOUBLE_TYPE;
			toType = Instruction.FLOAT_TYPE;
			break;
			
		//smaller types	
		case 0x91:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.BYTE_TYPE;
			break;
		case 0x92:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.CHAR_TYPE;
			break;
		case 0x93:
			fromType = Instruction.INTEGER_TYPE;
			toType = Instruction.SHORT_TYPE;
			break;
		}
		
		Instruction i = new Instruction(Variable.STACK,"()"+toType,Variable.STACK);
		i.setType(toType, fromType);
		out.add(i);
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x85 <= bytecode && bytecode <= 0x93;
	}
}
