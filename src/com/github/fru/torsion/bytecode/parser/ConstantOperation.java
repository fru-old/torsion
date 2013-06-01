package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.ClassFileConstantType;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public class ConstantOperation extends Body.AbstractParser{
	
	public ConstantOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		Identifier constant = new Identifier();
		
		if(bytecode == 0x01){
			constant.type.con(null);
		}else if(0x02 <= bytecode && bytecode <= 0x08 ){
			constant.type.con(new Integer(bytecode-0x03));
		}else if(bytecode == 0x09 || bytecode == 0x0A){
			constant.type.con(new Long(bytecode-0x09));
		}else if(0x0B <= bytecode && bytecode <= 0x0D ){
			constant.type.con(new Float(bytecode-0x0B));
		}else if(bytecode == 0x0E || bytecode == 0x0F){
			constant.type.con(new Double(bytecode-0x0E));
		}else if(bytecode == 0x10){
			constant.type.con(new Integer(byteStream.findNext()));
		}else if(bytecode == 0x11){
			constant.type.con(new Integer(byteStream.findShort()));
		}else if(bytecode == 0x12){
			int index = byteStream.findNext();
			constant.type.con(getConstant(index));
		}else if(bytecode == 0x13 || bytecode == 0x14){
			int index = byteStream.findShort();
			constant.type.con(getConstant(index));
		}
		stack.push(constant);
	}
	
	private Object getConstant(int index){
		ClassFileConstant constant = constants.get(index);
		Object out = constant.getValue();
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
