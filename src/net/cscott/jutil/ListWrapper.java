// ListWrapper.java, created Fri Jul 14 11:14:15 2000 by pnkfelix
// Copyright (C) 2000 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.List;

/**
 * {@link ListWrapper} is an abstract class designed to make it easier
 * to write wrappers around {@link List}s.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: ListWrapper.java,v 1.4 2006-10-30 19:58:06 cananian Exp $
 */
public abstract class ListWrapper<E> extends CollectionWrapper<E>
    implements List<E> {
    /** Implementations should return the wrapped {@link List} here. */
    @Override
    protected abstract List<E> wrapped();
    
    /** Creates a {@link ListWrapper}. */
    protected ListWrapper() { }
    
    public List<E> subList(int i, int j) {
	return wrapped().subList(i, j);
    }

    public E get(int i) {
	return wrapped().get(i);
    }

    public E set(int i, E o) {
	return wrapped().set(i, o);
    }

    public E remove(int i) {
	return wrapped().remove(i);
    }

    public java.util.ListIterator<E> listIterator(int i) {
	return wrapped().listIterator(i);
    }
    
    public java.util.ListIterator<E> listIterator() {
	return wrapped().listIterator();
    }

    public int lastIndexOf(Object o) {
	return wrapped().lastIndexOf(o);
    }

    public int indexOf(Object o) {
	return wrapped().indexOf(o);
    }

    public boolean addAll(int i, java.util.Collection<? extends E> c) {
	return wrapped().addAll(i, c);
    }

    public void add(int i, E o) {
	wrapped().add(i, o);
    }

}
