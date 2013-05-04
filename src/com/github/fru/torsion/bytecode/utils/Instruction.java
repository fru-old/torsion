package com.github.fru.torsion.bytecode.utils;

import java.util.ArrayList;
import java.util.List;

import com.github.fru.torsion.bytecode.utils.CodeList.Pointer;


public class Instruction {
	
	public static final String GOTO_INSTRUCTION = "<goto>";
	public static final String START_INSTRUCTION = "<start>";
	public static final String END_INSTRUCTION = "<end>";
	
	private final String operation;
	public Instruction(String operation) {
		this.operation = operation;
	}
	
	private ArrayList<Variable<?>> param = new ArrayList<Variable<?>>();

	public Instruction add(Variable<?> var){
		param.add(var);
		return this;
	}

	public Instruction clear(){
		this.param.clear();
		return this;
	}

	public Variable<?> getParam(int i){
		//modulo wrap around for negative numbers
		int b = this.param.size();
		if(b < 1)return null;
		i =  (i % b + b) % b;
		return this.param.get(i);
	}
	
	public void setParam(int i, Variable<?> value){
		while(this.param.size()<=i)this.param.add(null);
		int b = this.param.size();
		i =  (i % b + b) % b;
		this.param.set(i, value);
	}
	
	public String getOperation() {
		return operation;
	}
	
	public int paramCount(){
		return this.param.size();
	}

	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(Instruction.class)) return false;
		Instruction ot = (Instruction) o;
		if (ot.param == null ^ this.param == null) return false;
		if (this.param != null) {
			if (this.param.size() != ot.param.size()) return false;
			for (int i = 0; i < this.param.size(); i++) {
				if (!this.param.get(i).equals(ot.param.get(i))) return false;
			}
		}

		if (ot.operation == null ^ this.operation == null) return false;
		if (ot.operation != null && !ot.operation.equals(this.operation)) return false;

		return true;
	}
	
	public String toString() {
		return toString(0);
	}

	public String toString(int depth) {
		if(depth > 1)return "";
		String operands = "";
		boolean hasOut = getParam(-1).getType() != Type.REFERENCE;
		List<Variable<?>> p = hasOut ? param.subList(0, param.size()-1) : param; 
		for (Variable<?> in : p) {
			if(in == null){
				operands += " ,";
			}else{
				String op = in.toString();
				if(in.getValue() instanceof Pointer<?>){
					Object ref = ((Pointer<?>)in.getValue()).getData();
					if(ref instanceof Instruction){
						op = "( " +((Instruction)ref).toString(depth+1) + " )";//((Instruction)ref).getParam(1).toString();
					}
				}
				operands += " "+op + ",";
			}
		}
		if(operands.length()>0)operands = operands.substring(0, operands.length()-1);
		String out = (hasOut ? getParam(-1) + " " : "") + this.operation + operands;
		return out;
	}

	@Override
	public int hashCode() {
		return this.param.hashCode() >> 6 + this.operation.hashCode() >> 8;
	}
}
