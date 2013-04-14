package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;

public abstract class BytecodeParser {

	public abstract void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException;
	public abstract boolean isApplicable(int bytecode);
	
	
	static ConstantsBytecodeParser constant;
	static ConversionBytecodeParser conversion = new ConversionBytecodeParser();
	static OperationBytecodeParser operation = new OperationBytecodeParser();
	static StackBytecodeParser store = new StackBytecodeParser.Store();
	static StackBytecodeParser load = new StackBytecodeParser.Load();
	
	
	public static ArrayList<Instruction> parse(ByteInputStream byteStream, long offset, Map<Integer, ClassFileConstant> constants)
			throws EOFException {
		if(constant == null || constant.constants != constants)constant = new ConstantsBytecodeParser(constants);
		
		ArrayList<Instruction> out = new ArrayList<Instruction>();
		out.add(new Instruction(null, ":", offset));
		
		int bytecode = byteStream.findNext();
		
		if(constant.isApplicable(bytecode))constant.parse(bytecode, byteStream, out);
		else if(conversion.isApplicable(bytecode))conversion.parse(bytecode, byteStream, out);
		else if(operation.isApplicable(bytecode))operation.parse(bytecode, byteStream, out);
		else if(store.isApplicable(bytecode))store.parse(bytecode, byteStream, out);
		else if(load.isApplicable(bytecode))load.parse(bytecode, byteStream, out);
		else{
			System.out.println("Could not parse: "+bytecode);
			
		}
		
		return out;
	}
}
