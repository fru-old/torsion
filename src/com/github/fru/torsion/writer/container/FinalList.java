package com.github.fru.torsion.writer.container;

import java.util.ArrayList;
import java.util.Collection;

public class FinalList<A> extends ArrayList<A>{

	private static final long serialVersionUID = 6893925860048833309L;

	final String MESSAGE = "This list has been set immutable.";
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
	public boolean remove(Object key) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.remove(key);
	}
	
	@Override
	public A remove(int index) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.remove(index);
	}
	
	@Override
	public A set(int index, A element) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.set(index, element);
	}
	
	@Override
	public boolean add(A element) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.add(element);
	}
	
	@Override
	public void add(int index, A element) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		super.add(index,element);
	}
	
	@Override
	public boolean addAll(Collection<? extends A> c) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends A> c) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		return super.addAll(index, c);
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		if(isImmuatable)throw new RuntimeException(MESSAGE);
		super.removeRange(fromIndex, toIndex);
	}
}
