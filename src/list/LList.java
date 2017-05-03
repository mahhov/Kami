package list;

import java.util.Iterator;

public final class LList<T> implements Iterable<LList<T>> {
	private LList<T> next, prev;
	public T node;
	
	public LList() {
	}
	
	private LList(LList<T> next, LList<T> prev, T node) {
		this.next = next;
		this.prev = prev;
		this.node = node;
	}
	
	// adds to front
	public final LList<T> add(T node) {
		LList<T> r = new LList<>(this, null, node);
		this.prev = r;
		return r;
	}
	
	// returns head.next if removing head
	public final LList<T> remove(LList<T> lList) {
		if (lList.next != null)
			lList.next.prev = lList.prev;
		if (lList.prev != null)
			lList.prev.next = lList.next;
		if (this == lList)
			return next;
		return this;
	}
	
	public final Iterator<LList<T>> iterator() {
		return new LListIterator();
	}
	
	public final Iterable<LList<T>> reverseIterator() {
		return new Iterable<LList<T>>() {
			public final Iterator<LList<T>> iterator() {
				return new LListReverseIterator();
			}
		};
		
		// todo: which is better?
		//		return () -> new LListReverseIterator();
		//		return LListReverseIterator::new;
	}
	
	private final class LListIterator implements Iterator<LList<T>> {
		LList<T> cur;
		
		LListIterator() {
			cur = LList.this;
		}
		
		public final boolean hasNext() {
			return cur != null && cur.node != null;
		}
		
		public final LList<T> next() {
			LList<T> r = cur;
			cur = cur.next;
			return r;
		}
	}
	
	private final class LListReverseIterator implements Iterator<LList<T>> {
		LList<T> cur;
		
		LListReverseIterator() {
			cur = LList.this.prev;
		}
		
		public final boolean hasNext() {
			return cur != null && cur.node != null;
		}
		
		public final LList<T> next() {
			LList<T> r = cur;
			cur = cur.prev;
			return r;
		}
	}
}