package com.github.fru.torsion.jquery.utils;

import java.util.AbstractCollection;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImmutableCollectionProxy<E> extends AbstractCollection<E>{
	
	Iterable<E> proxy;
	
	public ImmutableCollectionProxy(Iterable<E> proxy){
		this.proxy = proxy;
	}

	@Override
	public Iterator<E> iterator() {
		final Iterator<E> before = proxy.iterator();
		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return before.hasNext();
			}

			@Override
			public E next() {
				return before.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}
	
	public static class IterateAllNodes implements Iterator<Node>{
		Node current;
		
		public IterateAllNodes(Document doc){
			current = doc;
		}
		

		private Node getNext(){
			Node c = current;
			if(c == null)return null;
			NodeList childreen = c.getChildNodes();
			if(childreen != null && childreen.getLength() > 0)return childreen.item(0);
			do{
				if(c.getNextSibling() != null)return c.getNextSibling();
				c = c.getParentNode();
			}while(c.getParentNode() != null);
			return null;
		}
		
		private Node nextCache = null;
		
		@Override
		public boolean hasNext() {
			return (nextCache = getNext()) != null;
		}

		@Override
		public Node next() {
			 Node out = nextCache != null ? nextCache : getNext();
			 nextCache = null;
			 return  current = out;
		}


		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
