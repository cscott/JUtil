// CollectionFactory.java, created Tue Oct 19 22:21:39 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/** {@link CollectionFactory} is a {@link Collection}
    generator.  Subclasses should implement constructions of specific
    types of {@link Collection}s.  
    <p>
    Note that since some types of {@link Collection}s have
    implicit constraints (such as {@link Set}s, which cannot
    contain more than one of the same element), code which uses the
    classes produced by {@link CollectionFactory}s must take care
    not to assume more than what is guaranteed by the
    {@link Collection} interface.
    <p>
    Note also that the current limitations on parametric types in
    Java mean that we can't easily type this class as
    <code>CollectionFactory&lt;C extends Collection&lt;V&gt;,V&gt;</code>,
    as <code>CollectionFactory&lt;Set&lt;V&gt;,V&gt;</code> is not
    a subtype of <code>CollectionFactory&lt;Collection&lt;V&gt;,V&gt;</code>,
    even though {@link Set} is a subtype of {@link Collection}.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: CollectionFactory.java,v 1.4 2006-10-30 19:58:05 cananian Exp $
 */
public abstract class CollectionFactory<V> {
    
    /** Creates a {@link CollectionFactory}. */
    public CollectionFactory() {
	
    }
    
    /** Generates a new, mutable, empty {@link Collection}. */
    public Collection<V> makeCollection() {
	return makeCollection(Collections.<V>emptySet());
    }

    /** Generates a new, mutable, empty {@link Collection}, using
	<code>initialCapacity</code> as a hint to use for the capacity
	for the produced {@link Collection}. */
    public Collection<V> makeCollection(int initialCapacity) {
	return makeCollection();
    }

    /** Generates a new, mutable {@link Collection}, using the
	elements of <code>c</code> as a template for its initial
	contents.  Note that the {@link Collection} returned is
	not a <i>view</i> of <code>c</code>, but rather a snapshot;
	changes to <code>c</code> are not reflected in the returned
	{@link Collection}. 
    */  
    public abstract Collection<V> makeCollection(Collection<? extends V> c);

    
    
}
