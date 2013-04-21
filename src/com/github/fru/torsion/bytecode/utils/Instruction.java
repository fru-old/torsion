package com.github.fru.torsion.bytecode.utils;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.fru.torsion.bytecode.utils.CodeList.Pointer;


public class Instruction {
	
	public static final String GOTO_INSTRUCTION = "<goto>";
	public static final String START_INSTRUCTION = "<start>";
	public static final String END_INSTRUCTION = "<end>";
	
	private ArrayList<Variable> ins = new ArrayList<Variable>();
	private final String operation;
	
	private Type[] type;

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
	
	public Instruction setType(Type... type){
		this.type = type;
		return this;
	}
	
	public Type[] getType(){
		return this.type;
	}
	
	public int paramCount(){
		return this.ins.size();
	}

	/*public Variable[] getInputs() {
		Collection<Variable> out = ins;
		//if(ins.size() > 0)out = ins.subList(0, ins.size()-1);
		return out.toArray(new Variable[0]);
	}*/

	public Pointer<Instruction> getReference() {
		return reference;
	}

	public void setReference(Pointer<Instruction> reference) {
		this.reference = reference;
	}

	public static enum VariableType {
		LOCAL, CONSTANT, ANNONYM, RETURN, LOCATION;
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
		String extra = this.operation.equals(Instruction.GOTO_INSTRUCTION) && this.reference != null ? " (" + this.reference.getData()+")" : "";
		String operands = "";
		for (Variable in : ins) {
			operands += (in == null ? "" : " "+in.toString()) + ",";
		}
		if(operands.length()>0)operands = operands.substring(0, operands.length()-1);
		String out = this.operation + extra + " " + operands;
		return out;
	}

	@Override
	public int hashCode() {
		return this.ins.hashCode() >> 6 + this.operation.hashCode() >> 8 + this.reference.hashCode() >> 16;
	}
}
