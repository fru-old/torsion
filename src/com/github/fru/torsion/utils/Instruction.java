package com.github.fru.torsion.utils;

import com.github.fru.torsion.utils.CodeList.Pointer;


public class Instruction {
	
	private final Variable out;
	private final Variable[] ins;
	private final String operation;
	
	private Pointer<Instruction> reference;
	
	public Instruction(Object out, String operation, Object... ins){
		this(new Variable(out),operation,Variable.convert(ins));
	}

	public Instruction(Variable out, String operation, Variable... ins){
		this.out = out;
		this.operation = operation;
		this.ins = ins;
		this.reference = null;
	}
	
	public Instruction(Variable label, String operation, Pointer<Instruction> reference){
		this.out = label;
		this.operation = operation;
		this.ins = null;
		this.reference = reference;
	}
	
	public Variable getOutput(){
		return out;
	}
	
	public String getOperation(){
		return operation;
	}
	
	public Variable[] getInputs(){
		return ins;
	}
	
	public Pointer<Instruction> getReference(){
		return reference;
	}
	
	public void setReference(Pointer<Instruction> reference){
		this.reference = reference;
	}
	
	public static enum VariableType{
		LOCAL, STACK, CONSTANT, ANNONYM, RETURN, LOCATION;
	}
	
	@Override
	public boolean equals(Object o){
		if(!o.getClass().equals(Instruction.class))return false;
		Instruction ot = (Instruction)o;
		if(ot.ins == null ^ this.ins == null)return false;
		if(this.ins != null){
			if(this.ins.length != ot.ins.length)return false;
			for(int i = 0; i < this.ins.length; i++){
				if(!this.ins[i].equals(ot.ins[i]))return false;
			}
		}
		
		if(ot.operation == null ^ this.operation == null)return false;
		if(ot.operation != null && !ot.operation.equals(this.operation))return false;
		
		if(ot.out == null ^ this.out == null)return false;
		if(ot.out != null && !ot.out.equals(this.out))return false;
		
		if(ot.reference == null ^ this.reference == null)return false;
		if(ot.reference != null && !ot.reference.equals(this.out))return false;

		return true;
	}
	
	public String toString(){
		String extra = this.operation.equals("goto") && this.reference != null ? " "+this.reference.getData().getOperation() : "";
		return this.out + " = " + this.operation + extra;
	}
	
	@Override
	public int hashCode(){
		return this.out.hashCode() + this.ins.hashCode() >> 6 + this.operation.hashCode() >> 8 + this.reference.hashCode() >> 16;
	}
	
	public static class Variable{
		
		public static final Variable STACK = new Variable();
		public static final Variable RETURN = new Variable();
		public static final Variable END = new Variable();
		
		private final Object value;
		
		public Variable(Object value){
			this.value = value;
		}
		
		public static Variable[] convert(Object[] ins){
			Variable[] vins = new Variable[ins.length];
			for(int i = 0; i < ins.length; i++)
				vins[i] = new Variable(ins[i]);
			return vins;
		}
		
		static int counter = 0;
		public Variable(){
			this(counter++);
		}
		
		public Object getValue(){
			return this.value;
		}
		
		public VariableType getType(){
			if(value == END)return VariableType.LOCATION;
			if(value == RETURN)return VariableType.RETURN;
			if(value == STACK)return VariableType.STACK;
			if(value instanceof Integer)return VariableType.LOCAL;
			if(value instanceof String)return VariableType.CONSTANT;
			return VariableType.ANNONYM;
		}
		
		public String toString(){
			VariableType type = getType();
			if(type == VariableType.STACK)return "STACK";
			if(type == VariableType.CONSTANT)return "CONST \""+value+"\"";
			if(type == VariableType.LOCAL)return "LOCAL "+value;
			String[] part = value.toString().split("@");
			return (part.length >= 1 ? part[1] : value.toString()); 
		}
		
		public boolean equals(Object o){
			if(!o.getClass().equals(Variable.class))return false;
			Variable ot = (Variable)o;
			if(ot.value == null ^ this.value == null)return false;
			if(this.value != null && !this.value.equals(ot.value) )return false;
			return true;
		}
	}
}
