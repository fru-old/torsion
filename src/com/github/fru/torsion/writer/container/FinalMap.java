package com.github.fru.torsion.writer.container;

import java.util.LinkedHashMap;
import java.util.Map;

public class FinalMap<A,B> extends LinkedHashMap<A,B> {

	private static final long serialVersionUID = 7760154334435667200L;

	final String MESSAGE = "This map has been set immutable.";
	boolean isImmuatable = false;
	public void setImmutable(){
		isImmuatable = true;
	}
	
	@Override
	public void clear() { 
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		super.clear();
	}
	
	@Override
	public B put(A key, B value) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.put(key,value);
	}
	
	@Override
	public void putAll(Map<? extends A, ? extends B> m) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		super.putAll(m);
	}
	
	@Override
	public B remove(Object key) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.remove(key);
	}
}
