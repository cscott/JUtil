// SetWrapper.java, created Fri Jul 14 14:16:01 2000 by pnkfelix
// Copyright (C) 2000 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Set;
/**
 * {@link SetWrapper} is analogous to
 * {@link CollectionWrapper}, specialized for {@link Set}s.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: SetWrapper.java,v 1.3 2006-10-30 19:58:07 cananian Exp $ */
public abstract class SetWrapper<E> extends CollectionWrapper<E>
    implements Set<E> {
    /** Implementations should return the wrapped {@link Set} here. */
    @Override
    protected abstract Set<E> wrapped();
    
    protected SetWrapper() { }
}
