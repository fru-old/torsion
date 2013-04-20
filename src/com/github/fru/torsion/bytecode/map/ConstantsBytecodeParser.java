package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Map;

import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.ClassFileConstant.Type;
import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public class ConstantsBytecodeParser extends BytecodeParser {

	Map<Integer, ClassFileConstant> constants;

	public ConstantsBytecodeParser(Map<Integer, ClassFileConstant> constants) {
		this.constants = constants;
	}

	@Override
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException {
		String type = null;
		String constant = null;
		
		if(bytecode == 0x01){
			type = Instruction.REFERENCE_TYPE;
			constant = "null";
		}else if(0x02 <= bytecode && bytecode <= 0x08 ){
			type = Instruction.INTEGER_TYPE;
			constant = ""+(bytecode-0x03);
		}else if(bytecode == 0x09 || bytecode == 0x0A){
			type = Instruction.LONG_TYPE;
			constant = ""+(bytecode - 0x09);
		}else if(0x0B <= bytecode && bytecode <= 0x0D ){
			type = Instruction.FLOAT_TYPE;
			constant = ""+(bytecode-0x0B);
		}else if(bytecode == 0x0E || bytecode == 0x0F){
			type = Instruction.DOUBLE_TYPE;
			constant = ""+(bytecode - 0x09);
		}else if(bytecode == 0x10){
			type = Instruction.INTEGER_TYPE;
			constant = ""+byteStream.findNext();
		}else if(bytecode == 0x11){
			type = Instruction.INTEGER_TYPE;
			constant = ""+byteStream.findShort();
		}else if(bytecode == 0x12){
			int location = byteStream.findNext();
			type = getConstantType(location);
			constant = getConstant(location);
		}else if(bytecode == 0x13 || bytecode == 0x14){
			int location = byteStream.findShort();
			type = getConstantType(location);
			constant = getConstant(location);
		}
		
		Instruction i = new Instruction("=",constant,Variable.STACK);
		i.setType(type);
		out.add(i);
	}
	
	private String getConstant(int index){
		ClassFileConstant constant = constants.get(index);
		String out = constant.getConstant();
		if(constant.getType() == Type.String){
			out = "\""+constants.get(constant.getRef1()).getConstant()+"\"";
		} 
		return out;
	}
	
	private String getConstantType(int index){
		ClassFileConstant constant = constants.get(index);
		if(constant.getType() == Type.String)return Instruction.REFERENCE_TYPE;
		if(constant.getType() == Type.Integer)return Instruction.INTEGER_TYPE;	
		if(constant.getType() == Type.Float)return Instruction.FLOAT_TYPE;	
		if(constant.getType() == Type.Double)return Instruction.DOUBLE_TYPE;	
		if(constant.getType() == Type.Long)return Instruction.LONG_TYPE;	
		return null;
	}

	@Override
	public boolean isApplicable(int bytecode) {
		return 0x01 <= bytecode && bytecode <= 0x14;
	}

}
