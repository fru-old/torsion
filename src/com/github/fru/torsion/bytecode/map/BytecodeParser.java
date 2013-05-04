package com.github.fru.torsion.bytecode.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public abstract class BytecodeParser {

	public abstract void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws IOException;
	public abstract boolean isApplicable(int bytecode);
	
	
	static ConstantsBytecodeParser constant;
	static InvocationBytecodeParser invocation;
	static ConversionBytecodeParser conversion = new ConversionBytecodeParser();
	static OperationBytecodeParser operation = new OperationBytecodeParser();
	static StackBytecodeParser store = new StackBytecodeParser.Store();
	static StackBytecodeParser load = new StackBytecodeParser.Load();
	static GotoBytecodeParser jump = new GotoBytecodeParser();
	static UnsupportedBytecodeParser unsup = new UnsupportedBytecodeParser();
	
	
	public static ArrayList<Instruction> parse(ByteInputStream byteStream, long offset, Map<Integer, ClassFileConstant> constants, Stack<Variable<?>> stack)
			throws IOException {
		if(constant == null || constant.constants != constants)constant = new ConstantsBytecodeParser(constants);
		if(invocation == null || invocation.constants != constants)invocation = new InvocationBytecodeParser(constants);
		
		ArrayList<Instruction> out = new ArrayList<Instruction>();
		out.add(new Instruction(":").add(new Variable<Long>(offset,Type.LOCATION)));
		
		int bytecode = byteStream.findNext();
		
		if(constant.isApplicable(bytecode))constant.parse(bytecode, byteStream, out, stack);
		else if(conversion.isApplicable(bytecode))conversion.parse(bytecode, byteStream, out, stack);
		else if(operation.isApplicable(bytecode))operation.parse(bytecode, byteStream, out, stack);
		else if(store.isApplicable(bytecode))store.parse(bytecode, byteStream, out, stack);
		else if(load.isApplicable(bytecode))load.parse(bytecode, byteStream, out, stack);
		else if(invocation.isApplicable(bytecode))invocation.parse(bytecode, byteStream, out, stack);
		else if(jump.isApplicable(bytecode))jump.parse(bytecode, byteStream, out, stack);
		else if(unsup.isApplicable(bytecode))unsup.parse(bytecode, byteStream, out, stack);
		else{
			System.out.println("Could not parse: "+bytecode);
			
		}
		
		return out;
	}
	
	
	public static boolean isBytecodeSupported(int bytecode){
		constant = new ConstantsBytecodeParser(null);
		invocation = new InvocationBytecodeParser(null);
		
		if(constant.isApplicable(bytecode))return true;
		else if(conversion.isApplicable(bytecode))return true;
		else if(operation.isApplicable(bytecode))return true;
		else if(store.isApplicable(bytecode))return true;
		else if(load.isApplicable(bytecode))return true;
		else if(invocation.isApplicable(bytecode))return true;
		else if(jump.isApplicable(bytecode))return true;
		else if(unsup.isApplicable(bytecode))return true;
		else{
			return false;
		}
	}
	
	
	
	
}
