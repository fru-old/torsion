package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Instruction.Variable;

public abstract class StackBytecodeParser extends BytecodeParser{
	
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException{
		bytecode = normaizeBytecode(bytecode) + 0x15;
		
		if(0x15 <= bytecode && bytecode <= 0x19){
			int local = byteStream.findNext();
			String type = Instruction.getType(bytecode-0x15);
			producePrimitive(type, local, out);
		}else if(0x1A <= bytecode && bytecode <= 0x2D ){
			int local = (bytecode-0x1A) % 4;
			String type = Instruction.getType((bytecode-0x1A) / 4);
			producePrimitive(type, local, out);
		}else if(0x2E <= bytecode && bytecode <= 0x35){
			String type = Instruction.getType(bytecode-0x2E);
			produceArray(type, out);
		}
	}
	
	protected abstract int normaizeBytecode(int bytecode);
	protected abstract void producePrimitive(String type, int local, ArrayList<Instruction> out);
	protected abstract void produceArray(String type, ArrayList<Instruction> out);
	
	public static class Load extends StackBytecodeParser{
		public boolean isApplicable(int bytecode){
			return 0x15 <= bytecode && bytecode <= 0x35;
		}
		
		@Override
		protected void producePrimitive(String type, int local, ArrayList<Instruction> out) {
			Instruction i = new Instruction("=", local,Variable.STACK);
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(String type, ArrayList<Instruction> out) {
			Instruction i = new Instruction("[]"+type, Variable.STACK, Variable.STACK,Variable.STACK);
			i.setType(Instruction.reduceType(type), Instruction.REFERENCE_TYPE, Instruction.INTEGER_TYPE);
			out.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x15;
		}
	}
	
	public static class Store extends StackBytecodeParser{
		public boolean isApplicable(int bytecode){
			return 0x36 <= bytecode && bytecode <= 0x56;
		}
		
		@Override
		protected void producePrimitive(String type, int local, ArrayList<Instruction> out) {
			Instruction i = new Instruction("=", Variable.STACK,local);
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(String type, ArrayList<Instruction> out) {
			Instruction i = new Instruction("[]", type, Variable.STACK, Variable.STACK, Variable.STACK,null);
			i.setType(null, Instruction.REFERENCE_TYPE, Instruction.INTEGER_TYPE, Instruction.reduceType(type));
			out.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x36;
		}
	}
	
}
