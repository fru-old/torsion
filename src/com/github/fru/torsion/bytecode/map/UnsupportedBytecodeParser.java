package com.github.fru.torsion.bytecode.map;

import java.io.IOException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;

public class UnsupportedBytecodeParser extends BytecodeParser{

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws IOException {
		throw new RuntimeException("The Bytecode ["+Integer.toHexString(bytecode)+"] is not supported!");
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(bytecode == 0xC2 || bytecode == 0xC3)return true;
		if(bytecode == 0xBA)return true;
		return false;
	}

}
