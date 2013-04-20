package com.github.fru.torsion.bytecode.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.fru.torsion.bytecode.utils.CodeList.Pointer;

public class Instruction {
	
	public static final String VOID_TYPE = "V";
	public static final String INTEGER_TYPE = "I";
	public static final String LONG_TYPE = "J";
	public static final String FLOAT_TYPE = "D";
	public static final String DOUBLE_TYPE = "F";
	public static final String REFERENCE_TYPE = "L";
	public static final String BYTE_TYPE = "B";
	public static final String CHAR_TYPE = "C";
	public static final String SHORT_TYPE = "S";
	public static final String ARRAY_TYPE = "[";

	private ArrayList<Variable> ins = new ArrayList<Variable>();
	private final String operation;
	
	private String[] type;

	private Pointer<Instruction> reference;

	public Instruction(String operation, Object... ins) {
		this(operation, Variable.convert(ins));
	}

	public Instruction(String operation, Variable... ins) {
		this.operation = operation;
		this.ins.addAll(Arrays.asList(ins));
		this.reference = null;
	}

	public Instruction(Variable label, String operation, Pointer<Instruction> reference) {
		this.operation = operation;
		this.ins.add(label);
		this.reference = reference;
	}

	/*public void setOutput(Variable out) {
		this.out = out;
	}*/
	
	public Instruction clear(){
		this.ins.clear();
		return this;
	}
	
	public Instruction add(String type, Variable name){
		this.ins.add(name);
		return this;
	}

	/*
	public Variable getOutput() {
		return out;
	}*/
	
	public Variable getOp(int i){
		//modulo wrap around for negative numbers
		int b = this.ins.size();
		if(b < 1)return null;
		i =  (i % b + b) % b;
		return this.ins.get(i);
	}
	
	public void setOp(int i, Variable value){
		int b = this.ins.size();
		i =  (i % b + b) % b;
		this.ins.set(i, value);
	}

	public String getOperation() {
		return operation;
	}
	
	public Instruction setType(String... type){
		this.type = type;
		return this;
	}
	
	public String[] getType(){
		return this.type;
	}

	public Variable[] getInputs() {
		Collection<Variable> out = ins;
		if(ins.size() > 0)out = ins.subList(0, ins.size()-1);
		return out.toArray(new Variable[0]);
	}

	public Pointer<Instruction> getReference() {
		return reference;
	}

	public void setReference(Pointer<Instruction> reference) {
		this.reference = reference;
	}

	public static enum VariableType {
		LOCAL, STACK, CONSTANT, ANNONYM, RETURN, LOCATION;
	}

	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(Instruction.class)) return false;
		Instruction ot = (Instruction) o;
		if (ot.ins == null ^ this.ins == null) return false;
		if (this.ins != null) {
			if (this.ins.size() != ot.ins.size()) return false;
			for (int i = 0; i < this.ins.size(); i++) {
				if (!this.ins.get(i).equals(ot.ins.get(i))) return false;
			}
		}

		if (ot.operation == null ^ this.operation == null) return false;
		if (ot.operation != null && !ot.operation.equals(this.operation)) return false;

		if (ot.reference == null ^ this.reference == null) return false;
		if (ot.reference != null && !ot.reference.equals(this.reference)) return false;

		return true;
	}

	public String toString() {
		String extra = this.operation.equals("goto") && this.reference != null ? " " + this.reference.getData().getOperation() : "";
		String operands = "";
		for (Variable in : ins) {
			operands += (in == null ? "" : in.toString()) + ", ";
		}
		String out = this.operation + extra + " " + operands;
		return out;
	}

	@Override
	public int hashCode() {
		return this.ins.hashCode() >> 6 + this.operation.hashCode() >> 8 + this.reference.hashCode() >> 16;
	}
	
	public static String reduceType(String type){
		if(type == BYTE_TYPE || type == CHAR_TYPE || type == SHORT_TYPE)return INTEGER_TYPE;
		return type;
	}
	
	private static final String[] typeOrder = {INTEGER_TYPE, LONG_TYPE, FLOAT_TYPE, DOUBLE_TYPE, REFERENCE_TYPE, BYTE_TYPE, CHAR_TYPE, SHORT_TYPE};
	
	public static String getType(int order){
		return typeOrder[order];
	}

	public static class Variable {

		public static final Variable STACK = new Variable();
		public static final Variable RETURN = new Variable();
		public static final Variable END = new Variable();

		private Object value;

		public Variable(Object value) {
			this.value = value;
		}

		public static Variable[] convert(Object[] ins) {
			Variable[] vins = new Variable[ins.length];
			for (int i = 0; i < ins.length; i++)
				vins[i] = new Variable(ins[i]);
			return vins;
		}

		static int counter = 0;

		public Variable() {
			this(counter++);
		}
		
		public static void offsetCounter(int count){
			counter += count;
		}

		public Object getValue() {
			return this.value;
		}

		// Bugfix: In a perfect world, value should actually never be STACK
		public boolean isStack() {
			return value == STACK || this == STACK;
		}

		public VariableType getType() {
			if (value == END) return VariableType.LOCATION;
			if (value == RETURN) return VariableType.RETURN;
			if (isStack()) return VariableType.STACK;
			if (value instanceof Long) return VariableType.LOCATION;
			if (value instanceof Integer) return VariableType.LOCAL;
			if (value instanceof String) return VariableType.CONSTANT;
			return VariableType.ANNONYM;
		}

		@Override
		public String toString() {
			VariableType type = getType();
			if (this == Variable.END) return "LOCATION end";
			if (this == Variable.RETURN) return "RETURN";
			if (type == VariableType.STACK) return "STACK";
			if (type == VariableType.CONSTANT) return "CONST " + value;
			if (type == VariableType.LOCAL) return "LOCAL " + value;
			if (type == VariableType.LOCATION) return "LOCATION " + value;
			if (value == null) return "";
			String[] part = value.toString().split("@");
			return (part.length > 1 ? part[1] : value.toString());
		}

		@Override
		public boolean equals(Object o) {
			if (!o.getClass().equals(Variable.class)) return false;
			Variable ot = (Variable) o;
			if (ot.value == null ^ this.value == null) return false;
			if (this.value != null && !this.value.equals(ot.value)) return false;
			return true;
		}

		@Override
		public int hashCode() {
			return this.value == null ? -1 : this.value.hashCode();
		}
	}
}
