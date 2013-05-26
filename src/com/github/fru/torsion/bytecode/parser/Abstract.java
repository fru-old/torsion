package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Identifier.Type;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

public abstract class Abstract {

	public abstract void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Identifier> stack, HashMap<Identifier, Type> locals) throws IOException;
	public abstract boolean isApplicable(int bytecode);
	
	
	static Constants constant;
	static Invocation invocation;
	static Conversion conversion = new Conversion();
	static Operation operation = new Operation();
	static Stack store = new Stack.Store();
	static Stack load = new Stack.Load();
	static Goto jump = new Goto();
	static Unsupported unsup = new Unsupported();
	
	
	public static ArrayList<Instruction> parse(ByteInputStream byteStream, long offset, Map<Integer, ClassFileConstant> constants, Stack<Variable<?>> stack)
			throws IOException {
		if(constant == null || constant.constants != constants)constant = new Constants(constants);
		if(invocation == null || invocation.constants != constants)invocation = new Invocation(constants);
		
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
		constant = new Constants(null);
		invocation = new Invocation(null);
		
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
