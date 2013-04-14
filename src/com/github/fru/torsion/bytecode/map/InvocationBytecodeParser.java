package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public class InvocationBytecodeParser extends BytecodeParser{
	
	Map<Integer, ClassFileConstant> constants;

	public InvocationBytecodeParser(Map<Integer, ClassFileConstant> constants) {
		this.constants = constants;
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException {
		ClassFileConstant constant = constants.get(byteStream.findShort());
		
		switch(bytecode){
		case 0xB2:
			out.add(new Instruction(Variable.STACK,"=static",constant));
			break;
		case 0xB3:
			out.add(new Instruction(constant,"=static",Variable.STACK));
			break;
		case 0xB4:
			out.add(new Instruction(Variable.STACK,"=field",constant));
			break;
		case 0xB5:
			out.add(new Instruction(constant,"=field",Variable.STACK));
			break;
		}
		
		switch(bytecode){
		case 0xB6:
			out.add(new Instruction(Variable.STACK,"invokevirtual",constant,Variable.STACK));
			break;
		case 0xB7:
			out.add(new Instruction(Variable.STACK,"invokespecial",constant,Variable.STACK));
			break;
		case 0xB8:
			out.add(new Instruction(Variable.STACK,"invokestatic",constant));
			break;
		case 0xB9:
			byteStream.findShort();
			out.add(new Instruction(Variable.STACK,"invokeinterface",constant,Variable.STACK));
			break;
		case 0xBA:
			byteStream.findShort();
			out.add(new Instruction(Variable.STACK,"invokedynamic",constant));
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
