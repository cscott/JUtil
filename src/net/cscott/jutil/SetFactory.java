// SetFactory.java, created Tue Oct 19 22:22:44 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/** <code>SetFactory</code> is a <code>Set</code> generator.
    Subclasses should implement constructions of specific types of
    <code>Set</code>s.
    <p>
    Note also that the current limitations on parametric types in
    Java mean that we can't easily type this class as
    <code>SetFactory&lt;S extends Set&lt;V&gt;,V&gt;</code>,
    as <code>SetFactory&lt;HashSet&lt;V&gt;,V&gt;</code> is not
    a subtype of <code>SetFactory&lt;Set&lt;V&gt;,V&gt;</code>,
    even though <code>HashSet</code> is a subtype of <code>Set</code>.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: SetFactory.java,v 1.3 2004-06-29 19:44:06 cananian Exp $
 */
public abstract class SetFactory<V> extends CollectionFactory<V> {
    
    /** Creates a <code>SetFactory</code>. */
    public SetFactory() {
        super();
    }
    
    public final Set<V> makeCollection() {
	return makeSet();
    }

    public final Set<V> makeCollection(Collection<? extends V> c) {
	return makeSet(c);
    }

    public final Set<V> makeCollection(int initCapacity) {
	return makeSet(initCapacity);
    }

    /** Generates a new, mutable, empty <code>Set</code>. */
    public Set<V> makeSet() {
	return makeSet(Collections.<V>emptySet());
    }

    /** Generates a new, mutable, empty <code>Set</code>, using
	<code>initialCapacity</code> as a hint to use for the capacity
	for the produced <code>Set</code>. */
    public Set<V> makeSet(int initialCapacity) {
	return makeSet();
    }

    /** Generates a new mutable <code>Set</code>, using the elements
	of <code>c</code> as a template for its initial contents. 
    */ 
    public abstract Set<V> makeSet(Collection<? extends V> c);
    
}
 
