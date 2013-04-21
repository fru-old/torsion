package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public abstract class StackBytecodeParser extends BytecodeParser{
	
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out) throws EOFException{
		bytecode = normaizeBytecode(bytecode) + 0x15;
		
		if(0x15 <= bytecode && bytecode <= 0x19){
			int local = byteStream.findNext();
			Type type = Type.getType(bytecode-0x15);
			producePrimitive(type, local, out);
		}else if(0x1A <= bytecode && bytecode <= 0x2D ){
			int local = (bytecode-0x1A) % 4;
			Type type = Type.getType((bytecode-0x1A) / 4);
			producePrimitive(type, local, out);
		}else if(0x2E <= bytecode && bytecode <= 0x35){
			Type type = Type.getType(bytecode-0x2E);
			produceArray(type, out);
		}
	}
	
	protected abstract int normaizeBytecode(int bytecode);
	protected abstract void producePrimitive(Type type, int local, ArrayList<Instruction> out);
	protected abstract void produceArray(Type type, ArrayList<Instruction> out);
	
	public static class Load extends StackBytecodeParser{
		public boolean isApplicable(int bytecode){
			return 0x15 <= bytecode && bytecode <= 0x35;
		}
		
		@Override
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out) {
			Instruction i = new Instruction("=", local,Variable.STACK);
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out) {
			Instruction i = new Instruction("[]", type, Variable.STACK, Variable.STACK,Variable.STACK);
			i.setType(Type.reduceType(type), Type.REFERENCE_TYPE, Type.INTEGER_TYPE);
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
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out) {
			Instruction i = new Instruction("=", Variable.STACK,local);
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out) {
			Instruction i = new Instruction("[]", type, Variable.STACK, Variable.STACK, Variable.STACK,null);
			i.setType(null, Type.REFERENCE_TYPE, Type.INTEGER_TYPE, Type.reduceType(type));
			out.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x36;
		}
	}
	
}
