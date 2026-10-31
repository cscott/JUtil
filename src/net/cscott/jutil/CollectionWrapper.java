// CollectionWrapper.java, created Fri Nov 12 18:06:32 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.Collection;

/**
 * {@link CollectionWrapper} is a class that acts as a wrapper
 * around another Collection, using it as its backing store.  This
 * class isn't meant for direct usage, but rather provides for an easy
 * way for developers to quickly add extra independent behavior to
 * their own specific Collections without having to reimplement all of
 * AbstractCollection's interface
 * 
 * @author  Felix S. Klock II &lt;pnkfelix@mit.edu&gt;
 */
public abstract class CollectionWrapper<E> implements Collection<E> {
    /** Implementations should return the wrapped {@link Collection} here. */
    protected abstract Collection<E> wrapped();

    /** Creates a {@link CollectionWrapper}. */
    protected CollectionWrapper() { }
    
    @Override
    public boolean add(E o) {
	return wrapped().add(o);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
	return wrapped().addAll(c);
    }

    @Override
    public void clear() {
	wrapped().clear();
    }
    
    @Override
    public boolean contains(Object o) {
	return wrapped().contains(o);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
	return wrapped().containsAll(c);
    }

    @Override
    public boolean isEmpty() {
	return wrapped().isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
	return wrapped().iterator();
    }

    @Override
    public boolean remove(Object o) {
	return wrapped().remove(o);
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
	return wrapped().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	return wrapped().retainAll(c);
    }

    @Override
    public int size() {
	return wrapped().size();
    }

    @Override
    public Object[] toArray() {
	return wrapped().toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
	return wrapped().toArray(a);
    }
    @Override
    public String toString() {
	return wrapped().toString();
    }
    @Override
    public boolean equals(Object o) {
	return wrapped().equals(o);
    }
    @Override
    public int hashCode() {
	return wrapped().hashCode();
    }
}
