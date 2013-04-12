package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;

public interface BytecodeParser {

	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException;
	public boolean isApplicable(int bytecode);
	
}
