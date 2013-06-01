package com.github.fru.torsion.bytecode.parser;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.github.fru.torsion.bytecode.ByteInputStream;
import com.github.fru.torsion.bytecode.ClassFileConstant;
import com.github.fru.torsion.bytecode.normalization.Body;
import com.github.fru.torsion.bytecode.normalization.Identifier;
import com.github.fru.torsion.bytecode.normalization.Identifier.LocalVariable;
import com.github.fru.torsion.bytecode.normalization.Instruction;

public abstract class StackOperation extends Body.AbstractParser{
	
	public StackOperation(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
		super(stack,constants,body,clazz);
	}
	
	public void parse(int bytecode, ByteInputStream byteStream, int location) throws EOFException {
		bytecode = normaizeBytecode(bytecode) + 0x15;
		
		if(0x15 <= bytecode && bytecode <= 0x19){
			int local = byteStream.nextByte();
			Class<?> type = StackOperation.getBasicType(bytecode-0x15);
			producePrimitive(location, type, local);
		}else if(0x1A <= bytecode && bytecode <= 0x2D ){
			int local = (bytecode-0x1A) % 4;
			Class<?> type = StackOperation.getBasicType((bytecode-0x1A) / 4);
			producePrimitive(location, type, local);
		}else if(0x2E <= bytecode && bytecode <= 0x35){
			Class<?> type = StackOperation.getBasicType(bytecode-0x2E);
			produceArray(location,type);
		}else if(bytecode == 0x84){
			int local = byteStream.nextByte();
			byte value = (byte)byteStream.nextByte();
			Instruction i = new Instruction(location,"+");
			Identifier l = new Identifier(new Identifier.LocalVariable(local));
			Identifier c = new Identifier();
			c.type.con(value);
			i.add(l).add(c).add(l);
			body.add(i);
			return;
		}
	}
	
	private static Class<?> getBasicType(int i) {
		return new Class<?>[]{int.class,long.class,float.class,
				double.class,null,byte.class,char.class,short.class}[i];
	}
	
	protected abstract int normaizeBytecode(int bytecode);
	protected abstract void producePrimitive(int location, Class<?> type, int local);
	protected abstract void produceArray(int location, Class<?> type);
	
	public static class Load extends StackOperation{
		
		public Load(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
			super(stack,constants,body,clazz);
		}
		
		public boolean isApplicable(int bytecode){
			return 0x15 <= bytecode && bytecode <= 0x35;
		}
		
		@Override
		protected void producePrimitive(int location, Class<?> type, int local) {
			Instruction i = new Instruction(location,"=");
			Identifier from = new Identifier(new LocalVariable(local));
			Identifier result = new Identifier();
			if(type != null)result.type.add(type);
			result.type.add(from);
			stack.push(result);
			i.add(result).add(from);
			body.add(i);
		}

		@Override
		protected void produceArray(int location, Class<?> type) {
			Instruction i = new Instruction(location, "fromarray");
			Identifier index = stack.pop();
			Identifier array = stack.pop();
			Identifier result = new Identifier();
			if(type != null)result.type.add(type);
			result.type.add(array).resolveArrayTypes();
			i.add(result);
			i.add(array);
			i.add(index);
			stack.push(result);
			body.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x15;
		}
	}
	
	public static class Store extends StackOperation{
		
		public Store(Stack<Identifier> stack, HashMap<Integer, ClassFileConstant> constants, ArrayList<Instruction> body, Class<?> clazz) {
			super(stack,constants,body,clazz);
		}
		
		public boolean isApplicable(int bytecode){
			return 0x36 <= bytecode && bytecode <= 0x56;
		}
		
		@Override
		protected void producePrimitive(int location, Class<?> type, int local) {
			Instruction i = new Instruction(location,"=");
			Identifier value = stack.pop();
			Identifier result = new Identifier(new LocalVariable(local));
			body.add(i.add(result).add(value));
		}

		@Override
		protected void produceArray(int location, Class<?> type) {
			Instruction i = new Instruction(location,"toarray");
			i.add(stack.pop()); //array
			i.add(stack.pop()); //index
			i.add(stack.pop()); //value
			body.add(i);
		}

		@Override
		protected int normaizeBytecode(int bytecode) {
			return bytecode - 0x36;
		}
	}
	
}
