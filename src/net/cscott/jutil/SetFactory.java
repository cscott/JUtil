// SetFactory.java, created Tue Oct 19 22:22:44 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** {@link SetFactory} is a {@link Set} generator.
    Implementations construct specific types of {@link Set}s.
    It is a functional interface, so a constructor which copies a
    collection is a {@link SetFactory}:
    <pre>SetFactory&lt;String&gt; sf = HashSet::new;</pre>
    A lambda or a method reference is not serializable unless it is
    cast to a serializable type, as in
    <code>(SetFactory&lt;V&gt; &amp; Serializable) HashSet::new</code>.
    The factories of {@link Factories} are serializable.
    <p>
    Note also that the current limitations on parametric types in
    Java mean that we can't easily type this class as
    <code>SetFactory&lt;S extends Set&lt;V&gt;,V&gt;</code>,
    as <code>SetFactory&lt;HashSet&lt;V&gt;,V&gt;</code> is not
    a subtype of <code>SetFactory&lt;Set&lt;V&gt;,V&gt;</code>,
    even though {@link HashSet} is a subtype of {@link Set}.
 * 
 * @author  Felix S. Klock II &lt;pnkfelix@mit.edu&gt;
 */
@FunctionalInterface
public interface SetFactory<V> extends CollectionFactory<V> {
    @Override
    default Set<V> makeCollection() {
	return makeSet();
    }

    @Override
    default Set<V> makeCollection(Collection<? extends V> c) {
	return makeSet(c);
    }

    @Override
    default Set<V> makeCollection(int initCapacity) {
	return makeSet(initCapacity);
    }

    /** Generates a new, mutable, empty {@link Set}. */
    default Set<V> makeSet() {
	return makeSet(Collections.<V>emptySet());
    }

    /** Generates a new, mutable, empty {@link Set}, using
	<code>initialCapacity</code> as a hint to use for the capacity
	for the produced {@link Set}. */
    default Set<V> makeSet(int initialCapacity) {
	return makeSet();
    }

    /** Generates a new mutable {@link Set}, using the elements
	of <code>c</code> as a template for its initial contents. 
    */ 
    Set<V> makeSet(Collection<? extends V> c);
}
