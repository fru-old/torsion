package com.github.fru.torsion.bytecode.utils;

import com.github.fru.torsion.bytecode.utils.Instruction.VariableType;

public class Variable {

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

	static int counter = 5000;

	public Variable() {
		this(counter++);
	}

	public Object getValue() {
		return this.value;
	}

	
	
	
	/*
	 * Variable types local actual type 
	 */
	
	
	public VariableType getType() {
		if (value instanceof Long) return VariableType.LOCATION;
		if (value instanceof Integer) return VariableType.LOCAL;
		if (value instanceof String) return VariableType.CONSTANT;
		return VariableType.ANNONYM;
	}

	@Override
	public String toString() {
		VariableType type = getType();
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