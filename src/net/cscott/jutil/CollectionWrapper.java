// CollectionWrapper.java, created Fri Nov 12 18:06:32 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.Collection;

/**
 * <code>CollectionWrapper</code> is a class that acts as a wrapper
 * around another Collection, using it as its backing store.  This
 * class isn't meant for direct usage, but rather provides for an easy
 * way for developers to quickly add extra independent behavior to
 * their own specific Collections without having to reimplement all of
 * AbstractCollection's interface
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: CollectionWrapper.java,v 1.3 2006-10-29 20:15:47 cananian Exp $
 */
public abstract class CollectionWrapper<E> implements Collection<E> {
    /** Implementations should return the wrapped {@link Collection} here. */
    protected abstract Collection<E> wrapped();

    /** Creates a <code>CollectionWrapper</code>. */
    protected CollectionWrapper() { }
    
    public boolean add(E o) {
	return wrapped().add(o);
    }

    public boolean addAll(Collection<? extends E> c) {
	return wrapped().addAll(c);
    }

    public void clear() {
	wrapped().clear();
    }
    
    public boolean contains(Object o) {
	return wrapped().contains(o);
    }
    
    public boolean containsAll(Collection<?> c) {
	return wrapped().containsAll(c);
    }

    public boolean isEmpty() {
	return wrapped().isEmpty();
    }

    public Iterator<E> iterator() {
	return wrapped().iterator();
    }

    public boolean remove(Object o) {
	return wrapped().remove(o);
    }
    
    public boolean removeAll(Collection<?> c) {
	return wrapped().removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
	return wrapped().retainAll(c);
    }

    public int size() {
	return wrapped().size();
    }

    public Object[] toArray() {
	return wrapped().toArray();
    }
    
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
