// PersistentMultiMapFactory.java, created Thu Jan 20 15:52:07 2005 by cananian
// Copyright (C) 2005 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;

import net.cscott.jutil.FilterIterator.Filter;

/**
 * A {@link PersistentMultiMapFactory} uses hash-consing to ensure that
 * the {@link MultiMap}s created by it maximally reuse space.
 * Equality tests between {@link MultiMap}s created by this factory are
 * constant-time.  Cloning a {@link MultiMap} created by this factory is
 * also constant-time.  The generated {@link MultiMap}s are
 * {@link Map}s of (Persistent)<code>Set</code>s, not {@link Map}s of
 * generalized {@link Collection}s (ie, no duplicate &lt;key,value&gt;
 * mappings are allowed).  The implementation is based on persistent
 * randomized treaps.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class PersistentMultiMapFactory<K,V> implements MultiMapFactory<K,V> {
    final MapAllocator<K,V> mapAllocator = new MapAllocator<K,V>();
    final SetAllocator<V> setAllocator = new SetAllocator<V>();
    final Comparator<K> keyComparator;
    final Comparator<V> valueComparator;

    /** Creates a {@link PersistentMultiMapFactory}. */
    public PersistentMultiMapFactory(Comparator<K> keyComparator,
				     Comparator<V> valueComparator) {
	this.keyComparator = keyComparator;
	this.valueComparator = valueComparator;
    }

    /** Generates a new unsynchronized mutable {@link MultiMap} which
     *  is based on persistent randomized treaps.  All {@link MultiMap}s
     *  created by this factory maximally reuse space, and have very
     *  fast comparison operations. */
    @Override
    public MultiMap<K,V> makeMultiMap() {
	return new MultiMapImpl();
    }
    class MultiMapImpl extends AbstractMultiMap<K,V> {
	MapNode<K,V> root = null;
	MultiMapImpl() { }
	MultiMapImpl(MapNode<K,V> root) { this.root = root; }
	@Override public boolean isEmpty() { return this.root==null; }
	@Override public int size() { return (root==null)?0:root.size; }
	@Override public int hashCode() { return (root==null)?0:root.mapHashCode; }
	@Override
	@SuppressWarnings("unchecked") // from the same factory, so of the same type
	public boolean equals(Object o) {
	    // maps from the same factory can be compared very quickly
	    if (o instanceof PersistentMultiMapFactory.MultiMapImpl &&
		factory() == ((MultiMapImpl)o).factory())
		// constant-time!
		return this.root == ((MultiMapImpl)o).root;
	    return super.equals(o);
	}
	// for the equals() implementation.
	private PersistentMultiMapFactory<K,V> factory() {
	    return PersistentMultiMapFactory.this;
	}
	@Override
	public void clear() {
	    this.root = null;
	}
	// constant-time!
	@Override public MultiMapImpl clone() { return new MultiMapImpl(this.root); }
	@Override
	@SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	public boolean containsKey(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, (K)key);
	    return np!=null && np.value.size()>0;
	}
	@Override
	@SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	public boolean contains(Object a, Object b) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, (K)a);
	    if (np==null) return false;
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    SetNode<V> v = SetNode.get(np.value, valueComparator, (V)b);
	    if (v==null) return false;
	    return true;
	}

	@Override
	@SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	public V get(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, (K)key);
	    if (np==null) return null; // not found.
	    // return arbitrary element of set.
	    return np.value.key;
	}
	@Override
	@SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	public V remove(Object key) {
	    // yuck, can't enforce that comparator will be able to handle
	    // this arbitrary key
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, (K)key);
	    if (np==null || np.value==null) return null; // nothing to remove.
	    this.root = MapNode.remove(this.root, keyComparator, np.key,
				       mapAllocator);
	    return np.value.key;
	}
	@Override
	@SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	public boolean remove(Object key, Object value) {
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, (K)key);
	    if (np==null) return false;
	    SetNode<V> v = np.value;
	    SetNode<V> v2 = SetNode.remove(v, valueComparator, (V)value,
					   setAllocator);
	    if (v==v2) return false;
	    this.root = MapNode.put(this.root, keyComparator, np.key, v2,
				    mapAllocator);
	    return true;
	}
	@Override
	@SuppressWarnings("unchecked") // from the same factory, so of the same type
	public boolean addAll(MultiMap<? extends K,? extends V> mm) {
	    if (!(mm instanceof PersistentMultiMapFactory.MultiMapImpl))
		return super.addAll(mm);
	    MultiMapImpl mmi = (MultiMapImpl) mm;
	    if (this.factory() != mmi.factory()) return super.addAll(mm);
	    // fast impl.
	    MapNode.Merger<SetNode<V>> merger=new MapNode.Merger<SetNode<V>>(){
		@Override
		public SetNode<V> merge(SetNode<V> oldv, SetNode<V> newv) {
		    return SetNode.putAll(oldv, newv,
					  valueComparator, setAllocator);
		}
	    };
	    MapNode<K,V> np= MapNode.putAll(this.root, mmi.root, keyComparator,
					    mapAllocator, merger);
	    if (np==root) return false;
	    this.root = np;
	    return true;
	}

	@Override
	public Set<V> getValues(K key) {
	    MapNode<K,V> np = MapNode.get(this.root, keyComparator, key);
	    return new ValuesSet(key, np==null?null:np.value);
	}

	// hashcode of a (multi)map is the sum of the map.entries in the
	// set (equiv, mm.entrySet().hashSet()), where Map.Entry.hashCode
	// is key.hash^value.hash (with 0s if key/value are null)
	// HOWEVER, since (x^y)+(x^z) != (2*x)^(y+z), we hack the hashcode
	// and use key.hash ^ value-as-set.hashCode()

	@Override
	public Collection<V> values() {
	    return new AbstractSet<V>() {
		@Override public int size() { return MultiMapImpl.this.size(); }
		@Override
		public Iterator<V> iterator() {
		    return new FilterIterator<SetNode<V>,V>
			(new CombineIterator<SetNode<V>>
			 (new FilterIterator<MapNode<K,V>,Iterator<SetNode<V>>>
			  (MapNode.iterator(root),
			   new Filter<MapNode<K,V>,Iterator<SetNode<V>>>() {
			      @Override
			      public Iterator<SetNode<V>> map(MapNode<K,V> n) {
				  return SetNode.iterator(n.value);
			      }
			  })),
			 new Filter<SetNode<V>,V>() {
			    @Override public V map(SetNode<V> n) { return n.key; }
			});
		}
	    };
	}

	@Override public MultiMapSet<K,V> entrySet() { return new EntrySet(); }
	private class EntrySet extends AbstractSet<Map.Entry<K,V>>
	    implements MultiMapSet<K,V> {
	    @Override public int size() { return MultiMapImpl.this.size(); }
	    @Override public MultiMapImpl asMap() { return asMultiMap(); }
	    @Override public MultiMapImpl asMultiMap() { return MultiMapImpl.this; }
	    @Override
	    public Iterator<Map.Entry<K,V>> iterator() {
		return new Iterator<Map.Entry<K,V>>() {
		    final Iterator<MapNode<K,V>> map_it=MapNode.iterator(root);
		    Iterator<SetNode<V>> set_it = null;
		    MapNode<K,V> map_last = null;
		    SetNode<V> set_last = null;
		    
		    @Override
		    public boolean hasNext() {
			while ((set_it==null || !set_it.hasNext()) &&
			       map_it.hasNext()) {
			    map_last = map_it.next();
			    set_it = SetNode.iterator(map_last.value);
			}
			return set_it!=null && set_it.hasNext();
		    }
		    @Override
		    public Map.Entry<K,V> next() {
			if (!hasNext())
			    throw new NoSuchElementException();
			set_last = set_it.next();
			return new NodeWrapper(map_last.key, set_last.key);
		    }
		    @Override
		    public void remove() {
			if (set_last==null)
			    throw new IllegalStateException();
			MultiMapImpl.this.remove(map_last.key, set_last.key);
			set_last=null;
		    }
		};
	    }
	}
	private class ValuesSet extends AbstractSet<V> {
	    final K key;
	    SetNode<V> setRoot;
	    ValuesSet(K key, SetNode<V> setRoot) {
		this.key = key; this.setRoot = setRoot;
	    }
	    private PersistentMultiMapFactory<K,V> factory() {
		return PersistentMultiMapFactory.this;
	    }
	    @Override public int size() { return setRoot==null?0:setRoot.size; }
	    @Override
	    public int hashCode() {
		return (setRoot==null)?0:setRoot.setHashCode;
	    }
	    @Override
	    @SuppressWarnings("unchecked") // from the same factory, so of the same type
	    public boolean equals(Object o) {
		// sets from the same factory can be compared very quickly
		if (o instanceof PersistentMultiMapFactory.MultiMapImpl.ValuesSet &&
		    factory() == ((ValuesSet)o).factory())
		    // constant-time!
		    return this.setRoot == ((ValuesSet)o).setRoot;
		return super.equals(o);
	    }
	    @Override
	    public boolean add(V v) {
		SetNode<V> v2 = SetNode.put(setRoot, valueComparator, v, v,
					    setAllocator);
		if (setRoot == v2) return false; // no change.
		MultiMapImpl.this.root = MapNode.put(root, keyComparator,
						     key, v2, mapAllocator);
		setRoot = v2;
		return true;
	    }
	    @Override
	    @SuppressWarnings("unchecked") // from the same factory, so of the same type
	    public boolean addAll(Collection<? extends V> c) {
		if (!(c instanceof PersistentMultiMapFactory.MultiMapImpl.ValuesSet))
		    return super.addAll(c);
		ValuesSet vs = (ValuesSet) c;
		if (vs.factory() != this.factory()) return super.addAll(c);
		// fast path:
		SetNode<V> v2 = SetNode.putAll(setRoot, vs.setRoot,
					       valueComparator, setAllocator,
					       null);
		if (setRoot == v2) return false; // no change.
		MultiMapImpl.this.root = MapNode.put(root, keyComparator,
						     key, v2, mapAllocator);
		setRoot = v2;
		return true;
	    }
	    @Override
	    public void clear() {
		if (setRoot==null) return; // nothing to do!
		MultiMapImpl.this.root = MapNode.remove(root, keyComparator,
							key, mapAllocator);
		setRoot = null;
		return;
	    }
	    @Override
	    @SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	    public boolean contains(Object o) {
		if (setRoot==null) return false;
		// unpleasant cast to V
		return null != SetNode.get(setRoot, valueComparator, (V)o);
	    }
	    @Override
	    @SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
	    public boolean remove(Object o) {
		if (setRoot==null) return false;
		SetNode<V> v2 = SetNode.remove(setRoot, valueComparator, (V)o,
					       setAllocator);
		if (setRoot == v2) return false; // no change.
		MultiMapImpl.this.root = MapNode.put(root, keyComparator,
						     key, v2, mapAllocator);
		setRoot = v2;
		return true;
	    }
	    // XXX FAST IMPLEMENTATION POSSIBLE.
	    //public boolean removeAll(Collection<?> c) { }
	    // XXX FAST IMPLEMENTATION POSSIBLE.
	    //public boolean retainAll(Collection<?> c) { }
	    // XXX FAST IMPLEMENTATION POSSIBLE.
	    //public boolean containsAll(Collection<?> c) { }
	    @Override
	    public Iterator<V> iterator() {
		final Iterator<SetNode<V>> it = SetNode.iterator(setRoot);
		return new Iterator<V>() {
		    SetNode<V> last = null;
		    @Override public boolean hasNext() { return it.hasNext(); }
		    @Override
		    public V next() {
			last = it.next();
			return last.key;
		    }
		    @Override
		    public void remove() {
			if (last==null)
			    throw new IllegalStateException();
			ValuesSet.this.remove(last.key);
			last=null;
		    }
		};
	    }
	}
	/** Wrapper for 'Node' which restores the proper 'equals' and
	 *  'hashCode' semantics for a {@link MultiMap.Entry}. */
	private class NodeWrapper extends PairMapEntry<K,V> {
	    NodeWrapper(K k, V v) { super(k, v); }
	    // whee, side-effect impervious!
	    @Override
	    public V setValue(V value) {
		MultiMapImpl.this.remove(getKey(), getValue());
		MultiMapImpl.this.add(getKey(), value);
		return super.setValue(value);
	    }
	}
    }
    // PersistentTreeNode subclasses
    private static class MapNode<K,V>
	extends PersistentTreeNode<MapNode<K,V>,K,SetNode<V>> {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	final SetNode<V> value;
	/** The hash code of a {@link java.util.MultiMap} with the
	 *  contents of the tree rooted at this node. */
	final int mapHashCode;
	/** Size of the tree rooted at this node. */
	final int size;
	MapNode(K key, SetNode<V> value, MapNode<K,V> left, MapNode<K,V> right)
	{
	    super(key, left, right);
	    this.value = value;
	    // xxx: hashcode not quite correct, but ^ and + don't distribute
	    this.mapHashCode = // this entry:
		(((key==null)?0:key.hashCode()) ^
		 ((value==null)?0:value.hashCode())) +
		((left==null)?0:left.mapHashCode) + // hash of left tree
		((right==null)?0:right.mapHashCode); // hash of right tree
	    this.size = value.size() + // this entry
		((left==null)?0:left.size) + // size of left tree
		((right==null)?0:right.size); // size of right tree
	}
	@Override public SetNode<V> getValue() { return value; }
	// override 'equals' and 'hashCode' to facilitate hash-consing.
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof MapNode<?,?> n)) return false;
	    return ((key==null)?n.key==null:key.equals(n.key)) &&
		// XXX: using value.equals() *may* produce surprising
		// behavior if equal value objects are distinguishable.
		// but this is necessary for fast equals/putAll/etc.
		((value==null)?n.value==null:value.equals(n.value)) &&
		left == n.left && right == n.right;
	}
	@Override public int hashCode() { return mapHashCode; }
    }
    private static class SetNode<V>
	extends PersistentTreeNode<SetNode<V>,V,V> {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	/** The hash code of a {@link java.util.Set} with the
	 *  contents of the tree rooted at this node. */
	final int setHashCode;
	/** Size of the tree rooted at this node. */
	final int size;
	SetNode(V key, SetNode<V> left, SetNode<V> right) {
	    super(key, left, right);
	    this.setHashCode = key.hashCode() + // this entry
		((left==null)?0:left.setHashCode) + // hash of left tree
		((right==null)?0:right.setHashCode); // hash of right tree
	    this.size = 1 + // this entry
		((left==null)?0:left.size) + // size of left tree
		((right==null)?0:right.size); // size of right tree
	    //assert this.size == PersistentTreeNode.size(this);
	}
	// override 'equals' and 'hashCode' to facilitate hash-consing.
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof SetNode<?> n)) return false;
	    return ((key==null)?n.key==null:key.equals(n.key)) &&
		left == n.left && right == n.right;
	}
	public int size() { return size; }
	@Override public int hashCode() { return setHashCode; }
    }
    /** Allocator uses a {@link WeakHashMap} to do hash consing. */
    static class MapAllocator<K,V>
	extends PersistentTreeNode.Allocator<MapNode<K,V>,K,SetNode<V>> {
	final WeakHashMap<MapNode<K,V>,WeakReference<MapNode<K,V>>> hashConsCache =
	    new WeakHashMap<MapNode<K,V>,WeakReference<MapNode<K,V>>>();
	@Override
	MapNode<K,V> newNode(K key, SetNode<V> value,
			     MapNode<K,V> left, MapNode<K,V> right) {
	    MapNode<K,V> n = new MapNode<K,V>(key, value, left, right);
	    WeakReference<MapNode<K,V>> nn = hashConsCache.get(n);
	    if (nn==null)
		hashConsCache.put(n,nn=new WeakReference<MapNode<K,V>>(n));
	    return nn.get();
	}

	@Override
	public String toString() {
	    return hashConsCache.toString();
	}
    }
    /** Allocator uses a {@link WeakHashMap} to do hash consing. */
    static class SetAllocator<V>
	extends PersistentTreeNode.Allocator<SetNode<V>,V,V> {
	final WeakHashMap<SetNode<V>,WeakReference<SetNode<V>>> hashConsCache =
	    new WeakHashMap<SetNode<V>,WeakReference<SetNode<V>>>();
	@Override
	SetNode<V> newNode(V key, V value,
			   SetNode<V> left, SetNode<V> right) {
	    // ignore value.
	    SetNode<V> n = new SetNode<V>(key, left, right);
	    WeakReference<SetNode<V>> nn = hashConsCache.get(n);
	    if (nn==null)
		hashConsCache.put(n,nn=new WeakReference<SetNode<V>>(n));
	    return nn.get();
	}

	@Override
	public String toString() {
	    return hashConsCache.toString();
	}
    }
}
