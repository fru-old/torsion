package com.github.fru.torsion.writer;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.github.fru.torsion.writer.container.FinalList;

public class FinalListTest {

	@Test(expected=RuntimeException.class)
	public void clearTest(){
		FinalList<String> l = new FinalList<String>();
		l.add("test");
		l.setImmutable();
		l.clear();
	}
	
	@Test(expected=RuntimeException.class)
	public void iterableTest(){
		FinalList<String> l = new FinalList<String>();
		l.add("test");
		l.setImmutable();
		Iterator<String> it = l.iterator();
		it.next();
		it.remove();
	}
	
	@Test(expected=RuntimeException.class)
	public void sublistTest(){
		FinalList<String> l = new FinalList<String>();
		l.add("test");
		l.setImmutable();
		List<String> it = l.subList(0, 1);
		it.clear();
		System.out.println(l.size());
	}
}
