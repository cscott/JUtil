// Pair.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * An immutable pair of values, either of which may be
 * <code>null</code>.  Two {@link Pair}s are equal when their left
 * values are equal and their right values are equal, so pairs can be
 * keys in hash tables.  A {@link Pair} is serializable if its values
 * are.
 * <p>
 * {@link Pair} replaces {@link Default.PairList}, which is a mutable
 * {@link java.util.List} of two {@link Object}s.  For a pair which is
 * a {@link java.util.Map.Entry}, use {@link PairMapEntry} or
 * {@link java.util.AbstractMap.SimpleImmutableEntry}.
 *
 * @param left the left element of the pair (head)
 * @param right the right element of the pair (tail)
 * @param <A> the type of the left element
 * @param <B> the type of the right element
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 * @since 2.0
 */
public record Pair<A,B>(A left, B right) implements java.io.Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;
}
