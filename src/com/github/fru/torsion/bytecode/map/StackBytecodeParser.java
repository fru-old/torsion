package com.github.fru.torsion.bytecode.map;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Stack;

import com.github.fru.torsion.bytecode.utils.ByteInputStream;
import com.github.fru.torsion.bytecode.utils.Instruction;
import com.github.fru.torsion.bytecode.utils.Type;
import com.github.fru.torsion.bytecode.utils.Variable;

public abstract class StackBytecodeParser extends BytecodeParser{
	
	public void parse(int bytecode, ByteInputStream byteStream, ArrayList<Instruction> out, Stack<Variable<?>> stack) throws EOFException{
		bytecode = normaizeBytecode(bytecode) + 0x15;
		
		if(0x15 <= bytecode && bytecode <= 0x19){
			int local = byteStream.findNext();
			Type type = Type.getBasicType(bytecode-0x15);
			producePrimitive(type, local, out, stack);
		}else if(0x1A <= bytecode && bytecode <= 0x2D ){
			int local = (bytecode-0x1A) % 4;
			Type type = Type.getBasicType((bytecode-0x1A) / 4);
			producePrimitive(type, local, out, stack);
		}else if(0x2E <= bytecode && bytecode <= 0x35){
			Type type = Type.getBasicType(bytecode-0x2E);
			produceArray(type, out, stack);
		}
	}
	
	protected abstract int normaizeBytecode(int bytecode);
	protected abstract void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable<?>> stack);
	protected abstract void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable<?>> stack);
	
	public static class Load extends StackBytecodeParser{
		public boolean isApplicable(int bytecode){
			return 0x15 <= bytecode && bytecode <= 0x35;
		}
		
		@Override
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable<?>> stack) {
			Instruction i = new Instruction("=").add(new Variable<Integer>(local,type)).add(stack.push(new Variable.Default(Type.reduceType(type))));
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable<?>> stack) {
			Variable<Type> info = new Variable<Type>(type, Type.TYPE);
			type = new Type.Array(type);
			Instruction i = new Instruction("[]").add(info).add(stack.pop()).add(stack.pop()).add(stack.push(new Variable.Default(type)));
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
		protected void producePrimitive(Type type, int local, ArrayList<Instruction> out, Stack<Variable<?>> stack) {
			Instruction i = new Instruction("=").add(stack.pop()).add(new Variable<Integer>(local,type));
			out.add(i);
		}

		@Override
		protected void produceArray(Type type, ArrayList<Instruction> out, Stack<Variable<?>> stack) {
			Variable<Type> info = new Variable<Type>(type, Type.TYPE);
			type = new Type.Array(type);
			Instruction i = new Instruction("[]").add(info).add(stack.pop()).add(stack.pop()).add(stack.pop()).add(null);
			out.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x36;
		}
	}
	
}
