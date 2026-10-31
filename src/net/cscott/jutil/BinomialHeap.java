// BinomialMap.java, created Sat Jun 19 17:58:19 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
/**
 * A {@link BinomialHeap} allows
 * O(lg n) time bounds for insert, minimum, extract-min, union,
 * decrease-key, and delete operations.  Implementation is based on
 * the description in <i>Introduction to Algorithms</i> by Cormen,
 * Leiserson, and Rivest, on page 400 and following.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class BinomialHeap<K,V> extends AbstractHeap<K,V> implements Cloneable {
    private static final boolean debug=false;

    Node<K,V> head=null;
    final Comparator<Map.Entry<K,V>> c; // convenience field.
    
    /** Constructs a new, empty {@link BinomialHeap}, sorted according
     *  to the keys' natural order. All keys inserted into the new map
     *  must implement the {@link Comparable} interface. O(1) time. */
    public BinomialHeap() { this(Collections.<Map.Entry<K,V>>emptySet(), null); }
    /** Constructs a new, empty {@link BinomialHeap}, sorted according
     *  to the given comparator. O(1) time. */
    public BinomialHeap(Comparator<K> c) { this(Collections.<Map.Entry<K,V>>emptySet(), c); }
    /** Constructs a new binomial heap with the same entries as the specified
     *  {@link Heap}. O(n) time. */
    public BinomialHeap(Heap<K,? extends V> h) { this(h.entries(), h.comparator()); }
    /** Constructs a binomial heap from a collection of
     *  {@link java.util.Map.Entry}s and a key comparator.  O(n) time. */
    public BinomialHeap(Collection<? extends Map.Entry<? extends K,? extends V>> collection, Comparator<K> comparator) {
	super(comparator);
	c = entryComparator();
	union(collection);
	if (debug) checkHeap();
    }

    /** Returns a mapping entry with minimal key.  Takes O(lg n) time. */
    @Override
    public Map.Entry<K,V> minimum() {
	if (debug) checkHeap();
	Node<K,V> y=null;
	// minimum has to be one of the roots.
	for (Node<K,V> x=this.head; x!=null; x=x.sibling)
	    if (y==null || c.compare(x.entry, y.entry) < 0)
		y=x;
	if (y==null) throw new java.util.NoSuchElementException();
	return y.entry;
    }
    /** Links the B_{k-1} tree rooted at node y to the B_{k-1} tree rooted
     *  at node z; that is, it makes z the parent of y.  Node z thus becomes
     *  the root of a B_k tree. O(1) time. */
    private void _link(Node<K,V> y, Node<K,V> z) {
	y.parent = z;
	y.sibling= z.child;
	z.child = y;
	z.degree++;
    }
    /** Merges the root lists of H1 and H2 into a single linked list sorted
     *  by degree into monotonically increasing order.  O(m) time, where
     *  m is the total number of roots in H1 and H2. [O(lg n) time] */
    private Node<K,V> _merge(Node<K,V> h1, Node<K,V> h2) {
	if (h1==null) return h2;
	if (h2==null) return h1;
	if (h1.degree<h2.degree) {
	    h1.sibling = _merge(h1.sibling, h2);
	    return h1;
	} else {
	    h2.sibling = _merge(h1, h2.sibling);
	    return h2;
	}
    }
    /** Add all the entries from the given heap to this one.
     *  The given heap will be empty on return.  Takes
     *  O(lg n) time if the given heap is a {@link BinomialHeap}
     *  and its entry comparator is the same as this one's.
     *  Otherwise, it takes O(n) time. */
    @Override
    public void union(Heap<? extends K,? extends V> h) {
	if (h instanceof BinomialHeap &&
	    entryComparator().equals(((BinomialHeap)h).entryComparator()))
	    union((BinomialHeap<? extends K, ? extends V>)h);
	else { union(h.entries()); h.clear(); }
    }
    // union a set of Map.Entry's
    private void union(Collection<? extends Map.Entry<? extends K,? extends V>> coll) {
	// add an n-entry set to an m-entry heap in O(n+lg(n+m)) time.
	// BUILD-HEAP in O(n) time by successive unions.
	ArrayList<BinomialHeap<K,V>> hal = new ArrayList<BinomialHeap<K,V>>(coll.size());
	for (Map.Entry<? extends K,? extends V> e : coll) {
	    BinomialHeap<K,V> bh = new BinomialHeap<K,V>(this.comparator());
	    bh.insert(e.getKey(), e.getValue());
	    hal.add(bh);
	}
	// now successively union-ify
	while (hal.size() > 1)
	    for (int i=0; i<hal.size()-1; i++)
		hal.get(i).union(hal.remove(hal.size()-1));

	if (hal.size()>0) this.union(hal.get(0)); // and now merge into this.
    }
    /** Merges all of the mappings from the specified map to this
     *  map. Note that duplicates <b>are</b> permitted. This operation
     *  takes O(lg n), where n is the number of entries in the resulting
     *  map. The comparator for m <b>must be identical</b> to the comparator
     *  for <code>this</code>. After calling <code>union()</code>, the
     *  specified map will be empty. */
    public void union(BinomialHeap<K,V> m) {
	assert m.c.equals(this.c);
	union(m.head);
	m.head = null; // empty out source map.
    }
    /** Union operation.  The specified node is the head of a binomial map. */
    void union(Node<K,V> n) { // the binomial-heap-union operation.
	if (debug) { checkHeap(head, c); checkHeap(n, c); }
	this.head = _merge(this.head, n);
	if (this.head==null) return; // hmm.  both source maps were empty.
	
	Node<K,V> prevx = null;
	Node<K,V> x = this.head;
	for (Node<K,V> nextx = x.sibling; nextx!=null; nextx=x.sibling) {
	    if ((x.degree != nextx.degree) ||
		((nextx.sibling!=null) && (nextx.sibling.degree==x.degree))) {
		prevx=x;
		x=nextx;
	    } else if (c.compare(x.entry, nextx.entry) <= 0) {
		x.sibling = nextx.sibling;
		_link(nextx, x);
	    } else {
		if (prevx==null)
		    this.head = nextx;
		else
		    prevx.sibling = nextx;
		_link(x, nextx);
		x = nextx;
	    }
	}
	if (debug) checkHeap();
    }

    /** Associates the specified value with the specified key in the map.
     *  If the map previously contained a mapping for this key, the old
     *  value is <b>not replaced</b>; both mappings will be present after
     *  the <code>insert()</code>.  O(lg n) time.
     * @return The {@link java.util.Map.Entry} added.
     */
    @Override
    public Map.Entry<K,V> insert(K key, V value) { // binomial-heap-insert
	if (debug) checkHeap();
	Entry<K,V> e = new Entry<K,V>(key, value);
	insert(e);
	return e;
    }
    @Override
    protected void insert(Map.Entry<K,V> me) {
	Entry<K,V> e = (Entry<K,V>) me;
	Node<K,V> x = new Node<K,V>(e);
	union(x);
	if (debug) checkHeap();
    }
    /** Remove and return the map entry with minimal key. O(lg n) time. */
    @Override
    public Map.Entry<K,V> extractMinimum() {
	if (debug) checkHeap();
	Node<K,V> x=((Entry<K,V>)minimum()).node; // find min node...
	// ..and remove it.
	_removeRoot(x);
	if (debug) checkHeap();
	// return minimum entry
	return x.entry;
    }
    /** Remove a tree root from the binomial heap. O(lg n) time. */
    private void _removeRoot(Node<K,V> x) {
	assert x.parent==null; // x is a root node.
	// remove from linked list of binomial tree roots.
	if (this.head==x) this.head=x.sibling;
	else for (Node<K,V> y=this.head; y!=null; y=y.sibling)
	    if (y.sibling==x) { y.sibling=x.sibling; break; }
	if (debug) checkHeap();
	// reverse linked list of children of x.
	Node<K,V> hprime=_reverse(null, x.child);
	if (debug) checkHeap(hprime, c);
	// union this and hprime.
	union(hprime);
	if (debug) checkHeap();
    }
    /** Reverse a linked list of siblings. */
    private Node<K,V> _reverse(Node<K,V> prev, Node<K,V> n) {
	if (n==null) return prev;
	Node<K,V> r = _reverse(n, n.sibling);
	n.parent = null;
	n.sibling = prev;
	return r;
    }

    /** Replace the key in the specified map entry with the specified
     *  <b>smaller</b> key.  O(lg n) time. */
    @Override
    public void decreaseKey(Map.Entry<K,V> me, K newkey) {
	if (debug) checkHeap();
	Node<K,V> x = ((Entry<K,V>) me).node;
	if (keyComparator().compare(newkey, x.entry.getKey()) > 0)
	    throw new UnsupportedOperationException("New key is greater than "+
						    "current key.");
	setKey(x.entry, newkey);
	_bubbleUp(x, false);
	// done.
	if (debug) checkHeap();
    }
    private Node<K,V> _bubbleUp(final Node<K,V> x, boolean delete) {
	Node<K,V> y = x;
	Node<K,V> z = y.parent;
	while ((z!=null) && (delete || (c.compare(y.entry, z.entry)<0))) {
	    // exchange fields of y and z.
	    _exchange(y, z);
	    y = z;
	    z = y.parent;
	}
	return y;
    }
    /** Exchange the {@link Entry}s in two nodes. */
    private void _exchange(Node<K,V> a, Node<K,V> b) {
	Entry<K,V> ea = a.entry, eb = b.entry;
	a.entry = eb; eb.node = a;
	b.entry = ea; ea.node = b;
    }
    /** Remove the specified map entry from the mapping. O(lg n) time. */
    @Override
    public void delete(Map.Entry<K,V> me) {
	if (debug) checkHeap();
	Node<K,V> x = ((Entry<K,V>) me).node;
	Node<K,V> y = _bubbleUp(x, true);
	// y is now root node to be removed.
	_removeRoot(y);
	if (debug) checkHeap();
    }

    /** Return the next node after the specified node in the iteration order.
     *  O(1) time. */
    Node<K,V> successor(Node<K,V> n) {
	if (n==null) return null;
	if (n.child!=null) return n.child;
	if (n.parent==null) return n.sibling; // first root only.
	return n.parent.sibling;
    }
    /** Return an unmodifiable collection of entries in this heap. */
    @Override
    public Collection<Map.Entry<K,V>> entries() {
	if (debug) checkHeap();
	return new AbstractCollection<Map.Entry<K,V>>() {
	    @Override public int size() { return BinomialHeap.this.size(); }
	    @Override
	    public Iterator<Map.Entry<K,V>> iterator() {
		return new UnmodifiableIterator<Map.Entry<K,V>>() {
		    Node<K,V> next = head;
		    @Override public boolean hasNext() { return next!=null; }
		    @Override
		    public Map.Entry<K,V> next() {
			Node<K,V> n=next; next = successor(next); return n.entry;
		    }
		};
	    }
	};
    }
    /** Returns the size of this map. O(lg n) time. */
    @Override
    public int size() {
	if (debug) checkHeap();
	int s=0;
	for (Node<K,V> nx=head; nx!=null; nx=nx.sibling)
	    s+=(1<<nx.degree);
	return s;
    }
    /** Removes all mappings from this map. O(1) time. */
    @Override
    public void clear() {
	this.head=null;
    }
    /** Returns <code>true</code> if this map contains no key-value mappings.*/
    @Override
    public boolean isEmpty() {
	return this.head==null;
    }

    /** Creates a new BinomialHeap with all the key-value pairs this one
     *  has.  O(n) time. */
    @Override
    public BinomialHeap<K,V> clone() {
	BinomialHeap<K,V> bm=new BinomialHeap<K,V>(comparator());
	bm.head=_clone(null, this.head);
	if (debug) { checkHeap(head, c); checkHeap(bm.head, bm.c); }
	return bm;
    }
    /** Recursively clone a node. */
    private Node<K,V> _clone(Node<K,V> parent, Node<K,V> n) {
	if (n==null) return null;
	Node<K,V> nn = new Node<K,V>(new Entry<K,V>(n.entry.getKey(), n.entry.getValue()));
	nn.degree = n.degree;
	nn.parent = parent;
	nn.child = _clone(nn, n.child);
	nn.sibling=_clone(parent, n.sibling);
	return nn;
    }

    /** Lookup a {@link java.util.Map.Entry} in the heap with key equal to
     *  the specified key.  O(n), although pruning is done on subtrees
     *  with root larger than the specified key.  What this means is
     *  that the smaller the key is, the faster this will run. */
    public Map.Entry<K,V> find(K key) {
	Node<K,V> x = find(head, key);
	return x.entry;
    }

    /** Find the node with key equal to the specified key. O(n), although
     *  pruning is done on subtrees with root larger than the specified key.
     *  What this means is that the smaller the key is, the faster this will
     *  run. */
    private Node<K,V> find(Node<K,V> n, K key) {
	if (n==null) return null;
	int cmp=keyComparator().compare(n.entry.getKey(), key);
	if (cmp==0) return n;
	Node<K,V> s=find(n.sibling, key);
	if (s!=null) return s;
	if (cmp > 0) return null; // all children will have larger keys.
	return find(n.child, key);
    }

    /** The underlying {@link java.util.Map.Entry} representation. */
    static final class Entry<K,V> extends PairMapEntry<K,V> {
	Node<K,V> node;
	Entry(K key, V value) { super(key, value); }
	K _setKey(K key) { return super.setKey(key); }
    }
    // to implement updateKey, etc...
    @Override
    protected final K setKey(Map.Entry<K,V> me, K newkey) {
	Entry<K,V> e = (Entry<K,V>) me;
	return e._setKey(newkey);
    }
    /** The underlying node representation for the binomial heap */
    static final class Node<K,V> {
	Node<K,V> parent;
	Entry<K,V> entry;
	int degree;
	Node<K,V> child, sibling; // left child, right sibling.
	/*-----------------------------*/
	Node(Entry<K,V> e) {
	    this.entry = e;
	    this.entry.node = this;
	    this.degree = 0;
	}
	@Override
	public String toString() {
	    return "<"+entry.toString()+", "+
		"degree: "+degree+", "+
		"parent key: "
		+((parent!=null)?parent.entry.getKey().toString():"(nil)")+", "+
		"child key: "
		+((child!=null)?child.entry.getKey().toString():"(nil)")+", "+
		"sibling key: "
		+((sibling!=null)?sibling.entry.getKey().toString():"(nil)")+">";
	}
    }
    /*-- debugging functions --*/
    private void checkHeap() { checkHeap(this.head, this.c); }
    private static <K,V> void checkHeap(Node<K,V> n, Comparator<Map.Entry<K,V>> c) {
	assert isHeapOrdered(n, c);
    }
    private static <K,V> boolean isTreeOrdered(Node<K,V> n, Comparator<Map.Entry<K,V>> c) {
	assert debug;
	if (n.parent==null) // special rules for root.
	    return 
		(n.sibling==null || n.sibling.parent==null) &&
		(n.child==null ||
		 (isTreeOrdered(n.child, c) &&
		  n.degree==n.child.degree+1 &&
		  n==n.child.parent)) &&
		((n.child==null) == (n.degree==0));
	// rules for non-root nodes.
	if (! (c.compare(n.entry, n.parent.entry) >= 0)) return false;
	if (n.sibling!=null) {
	    if (! isTreeOrdered(n.sibling, c)) return false;
	    if (! (n.degree==n.sibling.degree+1)) return false;
	    if (! (n.parent==n.sibling.parent)) return false;
	}
	if (n.child!=null) {
	    if (! isTreeOrdered(n.child, c)) return false;
	    if (! (n.degree==n.child.degree+1)) return false;
	    if (! (n==n.child.parent)) return false;
	}
	return (n.degree==0)
	    ?((n.child==null) && (n.sibling==null))
	    :((n.child!=null) && (n.sibling!=null));
    }
    private static <K,V> boolean isHeapOrdered(Node<K,V> n, Comparator<Map.Entry<K,V>> c) {
	assert debug;
	if (n==null) return true;
	return (n.parent==null) && // all top-level trees
	    isTreeOrdered(n, c) && // each tree in set is well-formed
	    ((n.sibling==null) ||  // either left-most, or
	     ((n.degree < n.sibling.degree) && // strictly increasing
	      isHeapOrdered(n.sibling, c)));   // and sibling well-formed.
    }
}
