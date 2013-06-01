package com.github.fru.torsion.main;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFile;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Body.AbstractParser;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.parser.ConstantOperation;
import com.github.fru.torsion.bytecode.parser.ConversionOperation;
import com.github.fru.torsion.bytecode.parser.GotoOperation;
import com.github.fru.torsion.bytecode.parser.InvocationOperation;
import com.github.fru.torsion.bytecode.parser.OtherOperation;
import com.github.fru.torsion.bytecode.parser.PrimitiveOperation;
import com.github.fru.torsion.bytecode.parser.StackOperation;
import com.github.fru.torsion.bytecode.parser.TypeDependentOperation;

public class Main_ByteCode {

	public static void main(String... args) throws Exception {
		HashMap<AccessibleObject, Body> result = ClassFile.parse(C.class);
		for(AccessibleObject o : result.keySet()){
			System.out.println("+++ "+o+" +++");
			System.out.println(result.get(o).toString());
			System.out.println("");
		}
		
		Stack<Identifier> stack = new Stack<Identifier>();
		HashMap<Integer, ClassFileConstant> constants = new HashMap<Integer, ClassFileConstant>();
		ArrayList<Instruction> body = new ArrayList<Instruction>();
		Class<?> clazz = null;
		AbstractParser[] parsers = new AbstractParser[]{
	    		new InvocationOperation(stack, constants, body, clazz),
	    		new ConstantOperation(stack, constants, body, clazz),
	    		new StackOperation.Load(stack, constants, body, clazz),
	    		new StackOperation.Store(stack, constants, body, clazz),
	    		new ConversionOperation(stack, constants, body, clazz),
	    		new GotoOperation(stack, constants, body, clazz),
	    		new TypeDependentOperation(stack, constants, body, clazz),
	    		new PrimitiveOperation(stack, constants, body, clazz),
	    		new OtherOperation(stack, constants, body, clazz)
	    };
		for(int b = 0; b < 0xCA; b++){
			boolean print = true;
			for(AbstractParser p : parsers)if(p.isApplicable(b))print=false;
			if(print)System.out.println("TODO: 0x"+Integer.toHexString(b));
		}
		
	}

}
