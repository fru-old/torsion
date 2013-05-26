package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.ClassFileConstantType;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.MethodBody;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class ConstantOperation extends MethodBody.AbstractParser{
	
	public ConstantOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws EOFException {
		Variable<?> constant = null;
		
		if(bytecode == 0x01){
			constant = new Variable<Object>(null, Type.NULL);
		}else if(0x02 <= bytecode && bytecode <= 0x08 ){
			constant = new Variable<Integer>(bytecode-0x03, Type.INTEGER);
		}else if(bytecode == 0x09 || bytecode == 0x0A){
			constant = new Variable<Long>((long)bytecode-0x09, Type.LONG);
		}else if(0x0B <= bytecode && bytecode <= 0x0D ){
			constant = new Variable<Float>((float)bytecode-0x0B, Type.FLOAT);
		}else if(bytecode == 0x0E || bytecode == 0x0F){
			constant = new Variable<Double>((double)bytecode-0x0E, Type.DOUBLE);
		}else if(bytecode == 0x10){
			constant = new Variable<Integer>(byteStream.findNext(), Type.INTEGER);
		}else if(bytecode == 0x11){
			constant = new Variable<Integer>(byteStream.findShort(), Type.INTEGER);
		}else if(bytecode == 0x12){
			int location = byteStream.findNext();
			constant = new Variable<String>(getConstant(location),Type.getConstantType(location, constants));
		}else if(bytecode == 0x13 || bytecode == 0x14){
			int location = byteStream.findShort();
			constant = new Variable<String>(getConstant(location),Type.getConstantType(location, constants));
		}
		
		Instruction i = new Instruction("=").add(constant).add(stack.push(new Variable.Default(constant.getType())));
		out.add(i);
	}
	
	private String getConstant(int index){
		ClassFileConstant constant = constants.get(index);
		String out = constant.getConstant();
		if(constant.getType() == ClassFileConstantType.String){
			out = constants.get(constant.getRef1()).getConstant();
		} 
		return out;
	}
	
	

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x01 <= bytecode && bytecode <= 0x14;
	}

}
