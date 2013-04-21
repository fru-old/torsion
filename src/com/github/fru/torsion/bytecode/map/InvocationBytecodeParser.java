package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Variable;

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
			out.add(new Instruction("=static",constant,Variable.STACK));
			break;
		case 0xB3:
			out.add(new Instruction("=static",Variable.STACK,constant));
			break;
		case 0xB4:
			out.add(new Instruction("=field",constant,Variable.STACK));
			break;
		case 0xB5:
			out.add(new Instruction("=field",Variable.STACK,constant));
			break;
		}
		
		switch(bytecode){
		case 0xB6:
			out.add(new Instruction("invokevirtual",constant,Variable.STACK,Variable.STACK));
			break;
		case 0xB7:
			out.add(new Instruction("invokespecial",constant,Variable.STACK,Variable.STACK));
			break;
		case 0xB8:
			out.add(new Instruction("invokestatic",constant,Variable.STACK));
			break;
		case 0xB9:
			byteStream.findShort();
			out.add(new Instruction("invokeinterface",constant,Variable.STACK,Variable.STACK));
			break;
		case 0xBA:
			byteStream.findShort();
			out.add(new Instruction("invokedynamic",constant,Variable.STACK));
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
