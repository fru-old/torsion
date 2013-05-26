package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Variable;

public class Unsupported extends Abstract{

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws IOException {
		throw new RuntimeException("The Bytecode ["+Integer.toHexString(bytecode)+"] is not supported!");
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(bytecode == 0xC2 || bytecode == 0xC3)return true;
		if(bytecode == 0xBA)return true;
		return false;
	}

}
