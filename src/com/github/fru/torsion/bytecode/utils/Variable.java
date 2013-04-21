package com.github.fru.torsion.bytecode.utils;

import com.github.fru.torsion.bytecode.utils.Instruction.VariableType;

public class Variable {

	public static final Variable STACK = new Variable();
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
		if (type == VariableType.STACK) return "STACK";
		if (type == VariableType.CONSTANT) return "CONST " + value;
		if (type == VariableType.LOCAL) return ""+ value;
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