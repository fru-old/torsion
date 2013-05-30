package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Instruction;
import com.github.fru.torsion.bytecode.normalization.Body;

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
			//TODO implement
			//int location = byteStream.findNext();
			//constant = new Variable<String>(getConstant(location),Type.getConstantType(location, constants));
		}else if(bytecode == 0x13 || bytecode == 0x14){
			//int location = byteStream.findShort();
			//constant = new Variable<String>(getConstant(location),Type.getConstantType(location, constants));
		}
		
		stack.push(constant);
		
		//Instruction i = new Instruction("=").add(constant).add(stack.push(new Variable.Default(constant.getType())));
		//out.add(i);
	}
	
	/*private String getConstant(int index){
		ClassFileConstant constant = constants.get(index);
		String out = constant.getConstant();
		if(constant.getType() == ClassFileConstantType.String){
			out = constants.get(constant.getRef1()).getConstant();
		} 
		return out;
	}*/
	
	

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x01 <= bytecode && bytecode <= 0x14;
	}

}
