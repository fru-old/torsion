package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class OperationBytecodeParser extends BytecodeParser {
	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException {
		String operation = null;
		Type type = null;
		
		if(0x60 <= bytecode && bytecode <= 0x77){
			String[] operations = {"+","-","*","/","%","negate"};
			operation = operations[(bytecode-0x60)/4];
			type = Type.getType((bytecode-0x60)%4);
			
		}else if(0x78 <= bytecode && bytecode <= 0x83){
			String[] operations = {"shl","shr","ushr","and","or","xor"};
			operation = operations[(bytecode-0x78)/2];
			type = Type.getType((bytecode-0x78)%2);	
		}
		
		Instruction i;
		if(0x74 <= bytecode && bytecode <= 0x77){//Negation
			 i = new Instruction(operation,Variable.STACK,Variable.STACK);
		}else{
			i = new Instruction(operation,Variable.STACK,Variable.STACK,Variable.STACK);
		}
		i.setType(type);
		out.add(i);
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x60 <= bytecode && bytecode <= 0x83;
	}
}
