// FilterIterator.java, created Tue Feb 23 06:17:30 1999 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link FilterIterator} filters and maps a source
 * {@link Iterator} to generate a new one: it returns
 * <code>map.apply(o)</code> for each element <code>o</code> of the
 * source for which <code>filter.test(o)</code> is true.
 * <pre>
 * Iterator&lt;String&gt; names = FilterIterator.map(people, Person::name);
 * Iterator&lt;Person&gt; adults = FilterIterator.filter(people, p -&gt; p.age() &gt;= 18);
 * </pre>
 * A {@link java.util.stream.Stream} does the same, with
 * <code>stream.filter(filter).map(map).iterator()</code>.
 * <p>
 * Note that this implementation reads one element ahead, so if the
 * filter changes for an object 'o' between the time that is read
 * (when next() is called, returning the object preceding 'o', and
 * checking that 'o' satisfies the current filter) and the time when
 * hasNext() is called, 'o' will still be returned, regardless of what
 * the filter now says about it.  Thus, it is recommended that only
 * filters which remain consistent throughout the iteration be used. 
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class FilterIterator<A,B> implements Iterator<B> {
    final Iterator<? extends A> i;
    final Predicate<? super A> filter;
    final Function<? super A,? extends B> map;
    /** Creates a {@link FilterIterator} which returns
     *  <code>map.apply(o)</code> for each element <code>o</code> of
     *  <code>i</code> for which <code>filter.test(o)</code> is true.
     *  @since 2.0 */
    public FilterIterator(Iterator<? extends A> i,
			  Predicate<? super A> filter,
			  Function<? super A,? extends B> map) {
	this.i = i; this.filter = filter; this.map = map; advance();
    }
    /** Creates a {@link FilterIterator}.
     *  @deprecated Use
     *  {@link #FilterIterator(Iterator,Predicate,Function)}, or
     *  {@link #filter(Iterator,Predicate)} or
     *  {@link #map(Iterator,Function)}, with lambdas instead of a
     *  {@link Filter}. */
    @Deprecated(since="2.0")
    @SuppressWarnings("deprecation") // takes a Filter
    public FilterIterator(Iterator<A> i, Filter<A,B> f) {
	this(i, f::isElement, f::map);
    }
    /** Returns an {@link Iterator} over the elements of <code>i</code>
     *  for which <code>filter.test()</code> is true.
     *  @since 2.0 */
    public static <A> FilterIterator<A,A> filter(Iterator<? extends A> i,
						 Predicate<? super A> filter) {
	return new FilterIterator<A,A>(i, filter, Function.identity());
    }
    /** Returns an {@link Iterator} over <code>map.apply(o)</code> for
     *  each element <code>o</code> of <code>i</code>.
     *  @since 2.0 */
    public static <A,B> FilterIterator<A,B> map(Iterator<? extends A> i,
						Function<? super A,? extends B> map) {
	return new FilterIterator<A,B>(i, o -> true, map);
    }

    private A next = null;
    private boolean done = false;

    private void advance() {
	while (i.hasNext()) {
	    next = i.next();
	    if (filter.test(next))
		return; // found next element.
	}
	done = true; // found end of enumeration.
    }

    @Override
    public B next() {
	if (done) throw new NoSuchElementException();
	A o = next; advance(); return map.apply(o);
    }
    @Override
    public boolean hasNext() {
	return !done;
    }

    /** A filter and a mapping, for a {@link FilterIterator}.
     *  @deprecated Use a {@link Predicate} and a {@link Function}, which
     *  can be lambdas, with
     *  {@link FilterIterator#FilterIterator(Iterator,Predicate,Function)},
     *  {@link FilterIterator#filter(Iterator,Predicate)} or
     *  {@link FilterIterator#map(Iterator,Function)}. */
    @Deprecated(since="2.0")
    public static class Filter<A,B> { // default is an identity mapping.
	/** Return <code>true</code> if the specified element should be
	    included in the filtered enumeration. 
	 
	    <BR> Default implementation returns true for all
	    {@link Object}s (no filter).   
	 */
	public boolean isElement(A o) { return true; }

	/** Perform a mapping on elements from the source enumeration. 

	    <BR> Default implementation returns <code>o</code>
	    (identity mapping). 
	 */
	@SuppressWarnings("unchecked") // only safe if A extends B
	public B map(A o) { return (B) o; }
    }
}
