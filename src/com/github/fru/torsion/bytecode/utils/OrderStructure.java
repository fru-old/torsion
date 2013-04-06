package com.github.fru.torsion.bytecode.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class OrderStructure<T> {

	public final ArrayList<T> linearDS = new ArrayList<T>();

	public Iterable<T> inbetween(final T before, final T after) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {

				return inbetween(linearDS.indexOf(before), linearDS.indexOf(after));
			}
		};
	}

	public boolean isAfter(T current, T after) {
		return linearDS.indexOf(current) < linearDS.indexOf(after);
	}

	public void add(T newest) {
		linearDS.add(newest);
	}

	public void addAfter(T current, T after) {
		int i = linearDS.indexOf(current);
		linearDS.add(i, after);
	}

	public void addBefore(T current, T before) {
		int i = linearDS.indexOf(current);
		linearDS.add(i + 1, before);
	}

	private Iterator<T> inbetween(final int beforeIndex, final int afterIndex) {
		return new Iterator<T>() {
			int nextIndex = beforeIndex + 1;

			@Override
			public boolean hasNext() {
				return nextIndex < afterIndex && linearDS.size() > nextIndex;
			}

			@Override
			public T next() {
				return linearDS.get(nextIndex++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
