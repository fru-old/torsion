package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public abstract class StackBytecodeParser extends BytecodeParser{
	
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable> stack) throws EOFException{
		bytecode = normaizeBytecode(bytecode) + 0x15;
		
		if(0x15 <= bytecode && bytecode <= 0x19){
			int local = byteStream.findNext();
			Type type = Type.getType(bytecode-0x15);
			producePrimitive(type, local, out, stack);
		}else if(0x1A <= bytecode && bytecode <= 0x2D ){
			int local = (bytecode-0x1A) % 4;
			Type type = Type.getType((bytecode-0x1A) / 4);
			producePrimitive(type, local, out, stack);
		}else if(0x2E <= bytecode && bytecode <= 0x35){
			Type type = Type.getType(bytecode-0x2E);
			produceArray(type, out, stack);
		}
	}
	
	protected abstract int normaizeBytecode(int bytecode);
	protected abstract void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable> stack);
	protected abstract void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable> stack);
	
	public static class Load extends StackBytecodeParser{
		public boolean isApplicable(int bytecode){
			return 0x15 <= bytecode && bytecode <= 0x35;
		}
		
		@Override
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable> stack) {
			Instruction i = new Instruction("=", local,stack.push(new Variable()));
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable> stack) {
			Instruction i = new Instruction("[]", type, stack.pop(), stack.pop(), stack.push(new Variable()));
			i.setType(Type.reduceType(type), Type.NULL_TYPE, Type.INTEGER_TYPE);
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
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable> stack) {
			Instruction i = new Instruction("=", stack.pop(), local);
			i.setType(type);
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable> stack) {
			Instruction i = new Instruction("[]", type, stack.pop(), stack.pop(), stack.pop(), null);
			i.setType(null, Type.NULL_TYPE, Type.INTEGER_TYPE, Type.reduceType(type));
			out.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x36;
		}
	}
	
}
