// SetWrapper.java, created Fri Jul 14 14:16:01 2000 by pnkfelix
// Copyright (C) 2000 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Set;
/**
 * <code>SetWrapper</code> is analogous to
 * <code>CollectionWrapper</code>, specialized for <code>Set</code>s.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: SetWrapper.java,v 1.2 2006-10-29 20:15:47 cananian Exp $ */
public abstract class SetWrapper<E> extends CollectionWrapper<E>
    implements Set<E> {
    /** Implementations should return the wrapped {@link Set} here. */
    @Override
    protected abstract Set<E> wrapped();
    
    protected SetWrapper() { }
}
