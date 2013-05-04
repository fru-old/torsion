package com.github.fru.torsion.bytecode.utils;


public class Variable<T> {

	public static class Default extends Variable<Object>{
		static int counter = 5000;

		public Default(Type type) {
			super(counter++,type);
		}
	}
	
	private T value;
	private Type type;

	public Variable(T value, Type type) {
		this.value = value;
		this.type = type;
	}

	public T getValue() {
		return this.value;
	}
	
	public Type getType(){
		return type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!Variable.class.equals(o.getClass())) return false;
		Variable<?> ot = (Variable<?>) o;
		if (ot.value == null ^ this.value == null) return false;
		if (this.value != null && !this.value.equals(ot.value)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return this.value == null ? -1 : this.value.hashCode();
	}
	
	@Override
	public String toString() {
		return (value==null?"null":value.toString())+(type==null?"":("-"+type));
	}
}