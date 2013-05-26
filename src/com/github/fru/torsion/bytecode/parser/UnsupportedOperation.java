package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.MethodBody;

public class UnsupportedOperation extends MethodBody.AbstractParser{
	
	public UnsupportedOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws IOException {
		throw new RuntimeException("The Bytecode ["+Integer.toHexString(bytecode)+"] is not supported!");
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(bytecode == 0xC2 || bytecode == 0xC3)return true;
		if(bytecode == 0xBA)return true;
		return false;
	}

}
