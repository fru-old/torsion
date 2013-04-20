package com.github.fru.torsion.bytecode.utils;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

public class CodeList<T> extends AbstractList<T> {

	// TODO: optimization - spline list

	public static class Pointer<S> {
		private final S data;

		private Pointer<S> next = null;
		private Pointer<S> prev = null;

		public Pointer(S data) {
			this.data = data;
		}

		public S getData() {
			return data;
		}

		public Pointer<S> getNext() {
			return next;
		}

		public Pointer<S> getPrev() {
			return prev;
		}

		public Pointer<S> get(int count) {
			if (count <= 0) return this;
			if (this.getNext() == null) return null;
			return this.getNext().get(count - 1);
		}

		public void remove(CodeList<S> list) {
			move(list, null, null);
			if (list.size == 1 && list.first == this && list.last == this) {
				list.first = null;
				list.last = null;
				list.size = 0;
			}
		}

		public Pointer<S> addAfter(CodeList<S> list, S element) {
			Pointer<S> out = new Pointer<S>(element);
			out.move(list, this, this.next);
			return out;
		}

		public Pointer<S> addBefore(CodeList<S> list, S element) {
			Pointer<S> out = new Pointer<S>(element);
			out.move(list, this.prev, this);
			return out;
		}

		private void move(CodeList<S> list, Pointer<S> before, Pointer<S> after) {
			if (before == null && after == null) {
				if (list.first == null && list.last == null) {
					list.size = 1;
					list.last = list.first = this;
					return;
				} else {
					list.size--;
					if (list.reverseHashMap.containsKey(this)) list.hashMap.remove(list.reverseHashMap.get(this));
				}
			}

			// remove
			if (this == list.first) list.first = this.next;
			if (this == list.last) list.last = this.prev;
			if (this.prev != null) this.prev.next = this.next;
			if (this.next != null) this.next.prev = this.prev;
			if (this.prev == null && this.next == null) list.size++;

			// insert
			if (after == list.first) list.first = this;
			if (before == list.last) list.last = this;
			this.prev = before;
			this.next = after;
			if (this.prev != null) this.prev.next = this;
			if (this.next != null) this.next.prev = this;
		}
	}

	private Pointer<T> first;
	private Pointer<T> last;
	int size;
	private HashMap<Object, Pointer<T>> hashMap = new HashMap<Object, Pointer<T>>();
	private HashMap<Pointer<T>, Object> reverseHashMap = new HashMap<Pointer<T>, Object>();

	public void mark(Object key, Pointer<T> pointer) {
		if (reverseHashMap.containsKey(pointer)) {
			reverseHashMap.remove(pointer);
			hashMap.remove(key);
		}
		hashMap.put(key, pointer);
		reverseHashMap.put(pointer, key);
	}

	public Pointer<T> get(Object key) {
		return hashMap.get(key);
	}
	
	@Override
	public String toString(){
		StringBuilder out = new StringBuilder();
		for(T t : this){
			out.append(t.toString());
			out.append('\n');
		}
		if(out.length()>0)out.setLength(out.length()-1);
		return out.toString();
	}

	public AbstractList<Pointer<T>> getPointer() {
		final CodeList<T> parent = this;
		return new AbstractList<Pointer<T>>() {
			@Override
			public Pointer<T> get(int index) {
				return first.get(index);
			}

			@Override
			public int size() {
				return size;
			}

			@Override
			public Pointer<T> remove(int index) {
				Pointer<T> rem = first.get(index);
				rem.remove(parent);
				return rem;
			}
		};
	}

	@Override
	public T remove(int index) {
		Pointer<T> out = first.get(index);
		out.remove(this);
		return out.getData();

	}

	@Override
	public void add(int index, T element) {
		if (first == null) {
			if (index == 0) {
				last = first = new Pointer<T>(element);
				size = 1;
			}
			return;
		}
		if (index == 0) {
			first.addBefore(this, element);
		} else {
			Pointer<T> before = first.get(index - 1);
			if (before != null) before.addAfter(this, element);
		}
	}

	public void addAfter(Pointer<T> before, T element) {
		before.addAfter(this, element);
	}

	@Override
	public T set(int index, T element) {
		T out = remove(index);
		add(index, element);
		return out;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public T get(int index) {
		return first.get(index).getData();
	}

	public Pointer<T> addPointer(int index, T element) {
		add(index, element);
		return getPointer().get(index);
	}

	public Pointer<T> addPointer(T element) {
		return addPointer(size(), element);
	}

	public Iterable<Pointer<T>> reversePointer() {
		final ListIterator<Pointer<T>> l = this.getPointer().listIterator(this.size());
		return new Iterable<Pointer<T>>() {
			@Override
			public Iterator<Pointer<T>> iterator() {
				return new Iterator<Pointer<T>>() {
					@Override
					public boolean hasNext() {
						return l.hasPrevious();
					}

					@Override
					public Pointer<T> next() {
						return l.previous();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}
