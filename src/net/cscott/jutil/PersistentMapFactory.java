// PersistentMapFactory.java, created Thu May 29 17:34:07 2003 by cananian
// Copyright (C) 2003 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
/**
 * A {@link PersistentMapFactory} uses hash-consing to ensure that
 * the {@link Map}s created by it maximally reuse space.
 * Equality tests between {@link Map}s created by this factory are
 * constant-time.  Cloning a {@link Map} created by this factory is
 * also constant-time.  The implementation is based on persistent
 * randomized treaps.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: PersistentMapFactory.java,v 1.7 2006-10-30 19:58:06 cananian Exp $
 */
public class PersistentMapFactory<K,V> extends MapFactory<K,V> {
    final Allocator<K,V> allocator = new Allocator<K,V>();
    final Comparator<K> comparator;

    /** Creates a {@link PersistentMapFactory}.  Note that the keys
     *  must implement a good hashcode as well as being comparable. */
    public PersistentMapFactory(Comparator<K> comparator) {
	this.comparator = comparator;
    }

    /** Generates a new unsynchronized mutable {@link Map} which
     *  is based on persistent randomized treaps.  All {@link Map}s
     *  created by this factory maximally reuse space, and have very
     *  fast comparison operations. */
    public Map<K,V> makeMap(Map<? extends K,? extends V> mm) {
	return new MapImpl(mm);
    }
    class MapImpl extends AbstractMap<K,V> {
	Node<K,V> root = null;
	MapImpl(Map<? extends K,? extends V> mm) {
	    putAll(mm);
	}
	MapImpl(Node<K,V> root) { this.root = root; }
	public boolean isEmpty() { return this.root==null; }
	public int size() { return (root==null)?0:root.size; }
	public int hashCode() { return (root==null)?0:root.mapHashCode; }
	public boolean equals(Object o) {
	    // maps from the same factory can be compared very quickly
	    if (o instanceof PersistentMapFactory.MapImpl &&
		factory() == ((MapImpl)o).factory())
		// constant-time!
		return this.root == ((MapImpl)o).root;
	    return super.equals(o);
	}
	// for the equals() implementation.
	private PersistentMapFactory<K,V> factory() {
	    return PersistentMapFactory.this;
	}
	public void clear() {
	    this.root = null;
	}
	// constant-time!
	public MapImpl clone() { return new MapImpl(this.root); }
	public boolean containsKey(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    return (Node.get(this.root, comparator, (K)key)!=null);
	}
	public V get(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    Node<K,V> np = Node.get(this.root, comparator, (K)key);
	    return (np==null)?null:np.value;
	}
	void removeFast(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    this.root = Node.remove(this.root, comparator, (K)key, allocator);
	}
	public V remove(Object key) {
	    V oldValue = get(key);
	    removeFast(key);
	    return oldValue;
	}
	void putFast(K key, V value) {
	    this.root = Node.put(this.root, comparator, key, value, allocator);
	}
	public V put(K key, V value) {
	    V oldValue = get(key);
	    putFast(key, value);
	    return oldValue;
	}
	public void putAll(Map<? extends K,? extends V> mm) {
	    // special fast case for maps from the same factory
	    // maps from the same factory can be compared very quickly
	    if (mm instanceof PersistentMapFactory.MapImpl &&
		factory() == ((MapImpl)mm).factory())
		this.root = Node.putAll(this.root, ((MapImpl)mm).root,
					comparator, allocator);
	    else // slow case
		super.putAll(mm);
	}
	public MapSet<K,V> entrySet() { return new EntrySet(); }
	private class EntrySet extends AbstractSet<Map.Entry<K,V>>
	    implements MapSet<K,V> {
	    public int size() { return MapImpl.this.size(); }
	    public MapImpl asMap() { return MapImpl.this; }
	    public Iterator<Map.Entry<K,V>> iterator() {
		final Iterator<Node<K,V>> it = Node.iterator(root);
		return new Iterator<Map.Entry<K,V>>() {
		    Node<K,V> last = null;
		    public boolean hasNext() { return it.hasNext(); }
		    public Map.Entry<K,V> next() {
			last = it.next();
			return new NodeWrapper(last);
		    }
		    public void remove() {
			if (last==null)
			    throw new IllegalStateException();
			MapImpl.this.removeFast(last.getKey());
			last=null;
		    }
		};
	    }
	}
	/** Wrapper for 'Node' which restores the proper 'equals' and
	 *  'hashCode' semantics for a {@link java.util.Map.Entry}. */
	private class NodeWrapper extends AbstractMapEntry<K,V> {
	    Node<K,V> node;
	    NodeWrapper(Node<K,V> node) { this.node = node; }
	    public K getKey() { return node.key; }
	    public V getValue() { return node.value; }
	    // whee, side-effect impervious!
	    public V setValue(V value) {
		V oldValue = node.value;
		MapImpl.this.putFast(node.key, value);
		node = Node.get(root, comparator, node.key);
		return oldValue;
	    }
	}
    }
    // PersistentTreeNode subclass
    private static class Node<K,V>
	extends PersistentTreeNode<Node<K,V>,K,V> {
	final V value;
	/** The hash code of a {@link java.util.Map} with the
	 *  contents of the tree rooted at this node. */
	final int mapHashCode;
	/** Size of the tree rooted at this node. */
	final int size;
	Node(K key, V value, Node<K,V> left, Node<K,V> right) {
	    super(key, left, right);
	    this.value = value;
	    this.mapHashCode = this.entryHashCode() + // this entry
		((left==null)?0:left.mapHashCode) + // hash of left tree
		((right==null)?0:right.mapHashCode); // hash of right tree
	    this.size = 1 + // this entry
		((left==null)?0:left.size) + // size of left tree
		((right==null)?0:right.size); // size of right tree
	    //assert this.size == PersistentTreeNode.size(this);
	}
	public V getValue() { return value; }
	// override 'equals' and 'hashCode' to facilitate hash-consing.
	public boolean equals(Object o) {
	    if (!(o instanceof Node)) return false;
	    Node n = (Node) o;
	    return ((key==null)?n.key==null:key.equals(n.key)) &&
		// XXX: using value.equals() *may* produce surprising
		// behavior if equal value objects are distinguishable.
		// but this is necessary for fast equals/putAll/etc.
		((value==null)?n.value==null:value.equals(n.value)) &&
		left == n.left && right == n.right;
	}
	public int hashCode() { return mapHashCode; }
	public int entryHashCode() { return super.hashCode(); }
    }
    /** Allocator uses a {@link WeakHashMap} to do hash consing. */
    static class Allocator<K,V>
	extends PersistentTreeNode.Allocator<Node<K,V>,K,V> {
	final WeakHashMap<Node<K,V>,WeakReference<Node<K,V>>> hashConsCache =
	    new WeakHashMap<Node<K,V>,WeakReference<Node<K,V>>>();
	Node<K,V> newNode(K key, V value,
			  Node<K,V> left, Node<K,V> right) {
	    Node<K,V> n = new Node<K,V>(key, value, left, right);
	    WeakReference<Node<K,V>> nn = hashConsCache.get(n);
	    if (nn==null)
		hashConsCache.put(n,nn=new WeakReference<Node<K,V>>(n));
	    return nn.get();
	}

	public String toString() {
	    return hashConsCache.toString();
	}
    }
}
