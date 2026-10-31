// Heap.java, created Sat Feb 12 08:42:27 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
/**
 * {@link Heap}s support create, insert, minimum, extract-min,
 * union, decrease-key, and delete operations.  There are three primary
 * implementations, each with different expected run-times:<br>
 * <table class="striped">
 * <caption>
 * Running times for operations on three implementations of mergable
 * heaps.  The number of items in the heap(s) at the time of an operation
 * is denoted by <i>n</i>.
 * (From <i>Introduction to Algorithms</i> by Cormen, Leiserson, and Rivest.)
 * </caption>
 * <thead>
 * <tr><th scope="col">Procedure</th>
 *     <th scope="col">{@link BinaryHeap}<br>(worst-case)</th>
 *     <th scope="col">{@link BinomialHeap}<br>(worst-case)</th>
 *     <th scope="col">{@link FibonacciHeap}<br>(amortized)</th></tr>
 * </thead>
 * <tbody>
 * <tr><th scope="row"><span style="font-variant: small-caps">Make-Heap</span></th>
 *     <td style="text-align: center">&Theta;(1)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Insert</span></th>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center"><i>O</i>(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Minimum</span></th>
 *     <td style="text-align: center">&Theta;(1)</td>
 *     <td style="text-align: center"><i>O</i>(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Extract-Min</span></th>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center"><i>O</i>(lg <i>n</i>)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Union</span></th>
 *     <td style="text-align: center">&Theta;(<i>n</i>)</td>
 *     <td style="text-align: center"><i>O</i>(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Decrease-Key</span></th>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(1)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Update-Key</span></th>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 * </tr>
 * <tr><th scope="row"><span style="font-variant: small-caps">Delete</span></th>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center">&Theta;(lg <i>n</i>)</td>
 *     <td style="text-align: center"><i>O</i>(lg <i>n</i>)</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * All implementations of {@link Heap} should have a no-argument
 * constructor which implements the 
 * <span style="font-variant: small-caps">Make-Heap</span> operation in
 * the above-stated time bound.  In addition, certain implementations
 * may also have constructors which take a {@link Map} or
 * {@link Heap} and construct a heap in less time than it would
 * take to call <code>insert()</code> on each item on an initially-empty
 * {@link Heap}.
 *
 * Note that the 
 * <span style="font-variant: small-caps">Update-Key</span> operation is
 * typically implemented as a delete followed by an insert, which
 * often has worse performance than a
 * <span style="font-variant: small-caps">Decrease-Key</span> operation.
 * However, some algorithms need to increase keys as well as decrease
 * them; there's nothing you can do about that.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 * @see BinaryHeap
 * @see BinomialHeap
 * @see FibonacciHeap
 */
public interface Heap<K,V> {
    /** Inserts a node with the specified key and value into the
     *  {@link Heap}.  Returns the generated {@link java.util.Map.Entry}
     *  which may be stored and eventually passed back to
     *  <code>decreaseKey()</code> or <code>delete</code> to remove
     *  this node. */
    public Map.Entry<K,V> insert(K key, V value);
    /** Returns a mapping entry with minimal key.
     * @exception java.util.NoSuchElementException
     *            if the heap has no entries.
     */
    public Map.Entry<K,V> minimum();
    /** Remove and return a map entry with minimal key.
     * @exception java.util.NoSuchElementException
     *            if the heap has no entries.
     */
    public Map.Entry<K,V> extractMinimum();
    /** Merges all of the mappings from the specified {@link Heap} 
     *  into this {@link Heap}.  Note that duplicates <b>are</b>
     *  permitted.  After calling <code>union()</code>, the {@link Heap}
     *  passed in as an argument will be empty.<p>
     *  Note that usually efficient implementations of this method require
     *  that the {@link Heap} argument be from the same implementation
     *  as <code>this</code>. (That is, they must both be binomial heaps, or
     *  both fibonacci heaps, etc.)
     */
    public void union(Heap<? extends K,? extends V> h);
    /** Replace the key in the specified map entry with the specified
     *  <b>smaller</b> key.  */
    public void decreaseKey(Map.Entry<K,V> me, K newkey);
    /** Replace the key in the specified map entry with the specified key,
     *  which may be either larger or smaller than its current key. */
    public void updateKey(Map.Entry<K,V> me, K newkey);
    /** Remove the specified map entry from the mapping. */
    public void delete(Map.Entry<K,V> me);
    /** Returns <code>true</code> if this {@link Heap} has no more
     *  entries. */
    public boolean isEmpty();
    /** Removes all entries from this {@link Heap}. */
    public void clear();
    /** Returns the number of entries in this {@link Heap}. */
    public int size();
    /** Returns a collection view of all the {@link java.util.Map.Entry}s
     *  in this {@link Heap}. */
    public Collection<Map.Entry<K,V>> entries();
    /** Returns the hash code for this heap.  The hash code for this
     *  heap will be one greater than the hash code for the
     *  {@link Collection} returned by {@link #entries()}. */
    @Override public int hashCode();
    /** Compares the specified object with this heap for equality.
     *  Returns <code>true</code> iff the given object is also a
     *  {@link Heap} and the {@link Collection}s returned
     *  by their {@link #entries()} methods are equal using
     *  the <code>equals()</code> method of {@link Collection}.
     */
    @Override public boolean equals(Object o);
    /** Returns a string representation of this {@link Heap}. */
    @Override public String toString();

    /** Returns the comparator associated with this {@link Heap},
     *  or <code>null</code> if it uses its elements' natural ordering. */
    // XXX: Should this be Comparator<? super K> instead?
    public Comparator<K> comparator();
}
