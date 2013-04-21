package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.ClassFileConstantType;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public class ConstantsBytecodeParser extends BytecodeParser {

	Map<Integer, ClassFileConstant> constants;

	public ConstantsBytecodeParser(Map<Integer, ClassFileConstant> constants) {
		this.constants = constants;
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable> stack) throws EOFException {
		Type type = null;
		String constant = null;
		
		if(bytecode == 0x01){
			type = Type.REFERENCE_TYPE;
			constant = "null";
		}else if(0x02 <= bytecode && bytecode <= 0x08 ){
			type = Type.INTEGER_TYPE;
			constant = ""+(bytecode-0x03);
		}else if(bytecode == 0x09 || bytecode == 0x0A){
			type = Type.LONG_TYPE;
			constant = ""+(bytecode - 0x09);
		}else if(0x0B <= bytecode && bytecode <= 0x0D ){
			type = Type.FLOAT_TYPE;
			constant = ""+(bytecode-0x0B);
		}else if(bytecode == 0x0E || bytecode == 0x0F){
			type = Type.DOUBLE_TYPE;
			constant = ""+(bytecode - 0x09);
		}else if(bytecode == 0x10){
			type = Type.INTEGER_TYPE;
			constant = ""+byteStream.findNext();
		}else if(bytecode == 0x11){
			type = Type.INTEGER_TYPE;
			constant = ""+byteStream.findShort();
		}else if(bytecode == 0x12){
			int location = byteStream.findNext();
			type = Type.getConstantType(location, constants);
			constant = getConstant(location);
		}else if(bytecode == 0x13 || bytecode == 0x14){
			int location = byteStream.findShort();
			type = Type.getConstantType(location, constants);
			constant = getConstant(location);
		}
		
		Instruction i = new Instruction("=",constant,stack.push(new Variable()));
		i.setType(type);
		out.add(i);
	}
	
	private String getConstant(int index){
		ClassFileConstant constant = constants.get(index);
		String out = constant.getConstant();
		if(constant.getType() == ClassFileConstantType.String){
			out = "\""+constants.get(constant.getRef1()).getConstant()+"\"";
		} 
		return out;
	}
	
	

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x01 <= bytecode && bytecode <= 0x14;
	}

}
