package com.github.fru.torsion.bytecode.parser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public class OtherOperation extends Body.AbstractParser{
	
	public OtherOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws IOException {
		Instruction i;
		switch(bytecode){
		case 0xBC:
			i = new Instruction(location, "new");
			Identifier a = new Identifier();
			a.type.add(Array.newInstance(getType(byteStream.nextByte()), 0).getClass());
			i.add(a);
			i.add(stack.pop());
			stack.push(a);
			body.add(i);
			return;
		case 0xBE:
			i = new Instruction(location, "arraylength");
			Identifier a1 = stack.pop();
			Identifier a2 = new Identifier();
			a2.type.add(int.class);
			stack.push(a2);
			i.add(a2).add(a1);
			body.add(i);
			return;	
		case 0xBF:
			throw new RuntimeException("Throw is not supported");
		case 0xC2:
		case 0xC3:
			throw new RuntimeException("Locking using monitor not supported");
		}
	}
	
	private Class<?> getType(int i){
		switch(i){
		case 4: return boolean.class;
		case 5: return char.class;
		case 6: return float.class;
		case 7: return double.class;
		case 8: return byte.class;
		case 9: return short.class;
		case 10: return int.class;
		case 11: return long.class;
		default: throw new RuntimeException("Could not parse type "+i);
		}
	}

	@Override
	public boolean isApplicable(int bytecode) {
		if(bytecode == 0xBC || bytecode == 0xBE || bytecode == 0xBF)return true;
		if(bytecode == 0xC2 || bytecode == 0xC3)return true;
		return false;
	}

}
