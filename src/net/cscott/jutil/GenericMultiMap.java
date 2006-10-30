// GenericMultiMap.java, created Tue Nov  9 00:17:02 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * {@link GenericMultiMap} is a default implementation of a
 * {@link MultiMap}.  
 *
 * <P>   FSK: right now the implementation tries to preserve the
 *       property that if a key 'k' maps to an empty collection 'c' in
 *	 some MultiMap 'mm', then users of 'mm' will not be able to
 *	 see that 'k' is a member of the keySet for 'mm'.  However, it
 *	 does not preserve this property when mm.getValues(k) is used
 *	 as a means to operate on the state of 'mm', and it is not
 *	 clear to me whether one can even ensure that the property
 *	 can be maintained if arbitrary operations on mm.getValues(k)
 *	 are passed on to 'mm'.
 * 
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: GenericMultiMap.java,v 1.6 2006-10-30 19:58:05 cananian Exp $ */
public class GenericMultiMap<K,V> extends AbstractMultiMap<K,V> {
    
    // internal Map[KeyType -> Collection[ ValueType ]]
    private Map<K,Collection<V>> internMap;

    // constructs Collections as needed 
    private CollectionFactory<V> cf;
    
    // used by identity constructor
    private MapFactory<K,Collection<V>> mf;

    /** Creates a {@link MultiMap} using a {@link HashMap} for
	the map and {@link HashSet}s for the value collections.
	To gain more control over the specific sets/map used in
	internal representation of <code>this</code>, use the more
	specific {@link GenericMultiMap#GenericMultiMap(MapFactory,CollectionFactory) constructor }
	that takes {@link CollectionFactory}s.
    */
    public GenericMultiMap() {
	this(Factories.<K,Collection<V>>hashMapFactory(), Factories.<V>hashSetFactory());
    }

    /** Creates a {@link MultiMap} using a {@link HashMap} for
     *  the map and the specified {@link CollectionFactory} to
     *  create the value collections. */
    public GenericMultiMap(CollectionFactory<V> cf) {
	this(Factories.<K,Collection<V>>hashMapFactory(), cf);
    }

    /** Creates a {@link MultiMap} using the specified
     *  {@link MapFactory} to create the map and the specified
     *  {@link CollectionFactory} to create the value collections.
    */
    public GenericMultiMap(MapFactory<K,Collection<V>> mf, CollectionFactory<V> cf) {
	this.internMap = mf.makeMap();
	this.cf = cf;
	this.mf = mf;
    }
    
    /** Creates a {@link GenericMultiMap} from another
	{@link GenericMultiMap}.
	
	NOTE: I would make this ctor public, but I need to eliminate
	any issues with the Collection-values being shared between
	'this' and 'mm'.

    */
    GenericMultiMap(GenericMultiMap<K,V> mm) { 
	this.mf = mm.mf;
	this.cf = mm.cf;
	this.internMap = this.mf.makeMap(mm.internMap);
    }
	
    /** Makes a new {@link MultiMap} initialized with all of the
	{@link java.util.Map.Entry}s in <code>m</code>.
    */
    GenericMultiMap(Map<? extends K,? extends V> m) { 
	this();
	putAll(m);
    }

    // note: we'd like to maintain a separate 'size' field, but
    // we can't intercept direct modification to the sets returned
    // by getValues(key), which we'd need to do.  So size() and
    // isEmpty() are slower than they might otherwise be.
    public int size() {
	int count=0;
	for (Collection<V> c : internMap.values() )
	    count += c.size();
	return count;
    }

    public boolean isEmpty() {
	// we could return 'size()==0' but that's slow.
	for (Collection<V> c : internMap.values() )
	    if (c.size()>0)
		return false;
	return true;
    }
    
    public boolean containsKey(Object key) {
	Collection<V> s = internMap.get(key);
	return (s != null && s.size() != 0);
    }
    
    public boolean containsValue(Object value) {
	for (K k : internMap.keySet())
	    if (getValues(k).contains(value))
		return true;
	return false;
    }


    /** Returns some arbitrary value from the set of values to which
	this map maps the specified key.  Returns <code>null</code> if
	the map contains no mapping for the key; it's also possible
	that the map explicitly maps the key to <code>null</code>.
	The <code>containsKey</code> operation may be used to
	distinquish these two cases.
	
	Note that if only the <code>put</code> method is used to
	modify <code>this</code>, then <code>get</code> will operate
	just as it would in any other {@link Map}.
    */
    public V get(Object key) {
	Collection<V> s = internMap.get(key);
	if (s == null || s.size() == 0) {
	    return null;
	} else {
	    return s.iterator().next();
	}
    }

    /** Removes all mappings for this key from this map if present. 
	Returns some previous value associated with specified key, or
	<code>null</code> if there was no mapping for key.  
     */
    public V remove(Object key) {
	Collection<V> c = internMap.get(key);
	internMap.remove(key);
	return (c==null || c.size()==0) ? null : c.iterator().next();
    }

    /** Removes a mapping from key to value from this map if present.
	Note that if multiple mappings from key to value are permitted
	by this map, then only one is guaranteed to be removed.
	Returns true if <code>this</code> was modified as a result of
	this operation, else returns false.
    */
    public boolean remove(Object key, Object value) {
	Collection<V> c = internMap.get(key);
	boolean result = (c!=null) ? c.remove(value) : false;
	if (c!=null && c.size()==0) internMap.remove(key);
	return result;
    }

    public void clear() {
	internMap.clear();
    }

    /** Returns the collection of Values associated with
	<code>key</code>.  Modifications to the returned
	{@link Collection} affect <code>this</code> as well.  If 
	there are no Values currently associated with
	<code>key</code>, constructs a new, mutable, empty
	{@link Collection} and returns it.
	({@link MultiMap} specific operation). 
    */
    public Collection<V> getValues(final K key) {
	Collection<V> c = internMap.get(key);
	if (c == null) {
	    c = cf.makeCollection();
	    internMap.put(key, c);
	}
	return c;
    }

    /** Returns true if <code>a</code> has a mapping to <code>b</code>
	in <code>this</code>.
	({@link MultiMap} specific operation). 
    */
    public boolean contains(Object a, Object b) {
	Collection<V> c = internMap.get(a);
	if (c != null)
	    return c.contains(b);
	else
	    return false;
    }

    /** Returns a set view of the keys in this map. */
    public Set<K> keySet() {
	return keySet;
    }
    private final Set<K> keySet = new KeySet();
    
    /** Returns a collection view of the values contained in this
	map.  
    */
    public Collection<V> values() { 
	return valuesCollection;
    }
    private final Collection<V> valuesCollection = new ValuesCollection();

    /** Returns a set view of the mappings contained in this map.
	This view is fully modifiable; the elements are
	{@link java.util.Map.Entry}s.  The returned set is actually a
	{@link MultiMapSet}, from which you can get back the
	original {@link MultiMap}.
    */
    public MultiMapSet<K,V> entrySet() {
	return entrySet;
    }
    private final MultiMapSet<K,V> entrySet = new EntrySet();
    
    // here are the class declarations that make the key set, entry set and
    // values collection work.
    class KeySet extends AbstractSet<K> {
	public int size() { return internMap.keySet().size(); }
	public Iterator<K> iterator() {
	    return new Iterator<K>() {
		    Iterator<K> it = internMap.keySet().iterator();
		    K lastKey;
		    public boolean hasNext() { return it.hasNext(); }
		    public K next() { return (lastKey=it.next()); }
		    public void remove() {
			Collection<V> c = internMap.get(lastKey);
			it.remove();
		    }
		};
	}
	public boolean remove(Object o) {
	    boolean changed = contains(o);
	    GenericMultiMap.this.remove(o);
	    return changed;
	}
	// for efficiency.
	public boolean contains(Object o) {
	    // note that this is slightly different from MM.containsKey(o)
	    return internMap.containsKey(o);
	}
	public void clear() {
	    GenericMultiMap.this.clear();
	}
    }
    class ValuesCollection extends AbstractCollection<V> {
	public int size() { return entrySet.size(); }
	public Iterator<V> iterator() {
	    return new Iterator<V>() {
		final Iterator<Map.Entry<K,V>> it = entrySet.iterator();
		public boolean hasNext() { return it.hasNext(); }
		public V next() { return it.next().getValue(); }
		public void remove() { it.remove(); }
	    };
	}
	public boolean add(V o) {
	    throw new UnsupportedOperationException();
	}
	public boolean remove(Object o) {
	    throw new UnsupportedOperationException();
	}
	// other methods for efficiency.
	public boolean contains(Object o) {
	    return GenericMultiMap.this.containsValue(o);
	}
	public void clear() { entrySet.clear(); }
    }
    // common code
    class EntrySet extends AbstractCollection<Map.Entry<K,V>>
	implements MultiMapSet<K,V> {
	// these are methods in MultiMapSet
	public GenericMultiMap<K,V> asMap() { return asMultiMap(); }
	public GenericMultiMap<K,V> asMultiMap() { return GenericMultiMap.this; }
	// these methods aren't in the collections interface
	// (from classpath impl of AbstractSet)
	public boolean equals(Object o) {
	    if (o == this)
		return true;
	    else if (o instanceof Set && ((Set) o).size() ==
		     EntrySet.this.size())
		return EntrySet.this.containsAll((Collection/*<Map.Entry<K,V>>*/) o);
	    else
		return false;
	}
	public int hashCode() {
	    Iterator itr = EntrySet.this.iterator();
	    int size = EntrySet.this.size();
	    int hash = 0;
	    for (int pos = 0; pos < size; pos++)
		{
		    Object obj = itr.next();
		    if (obj != null)
			hash += obj.hashCode();
		}
	    return hash;
	}
	// get down to business: Implement AbstractCollection
	public int size() { return GenericMultiMap.this.size(); }
	public Iterator<Map.Entry<K,V>> iterator() {
	    return new Iterator<Map.Entry<K,V>>() {
		    Iterator<Map.Entry<K,Collection<V>>> mapit = internMap.entrySet().iterator();
		    Iterator<V> setit = Default.nullIterator();
		    Iterator<V> lastit;
		    K key;
		    { advance(); }
		    public boolean hasNext() { return setit.hasNext(); }
		    public Map.Entry<K,V> next() {
			V o = setit.next();
			K k = key;
			advance();
			return new PairMapEntry<K,V>(k, o) {
				    public V setValue(V newValue) {
					V oldValue = getValue();
					GenericMultiMap.this.remove(getKey(),
								    oldValue);
					GenericMultiMap.this.add(getKey(),
								 newValue);
					super.setValue(newValue);
					return oldValue;
				    }
				};
		    }
		    void advance() {
			lastit = setit;
			while (!setit.hasNext() && mapit.hasNext()) {
			    Map.Entry<K,Collection<V>> me = mapit.next();
			    key = me.getKey();
			    Collection<V> c = me.getValue();
			    setit = c.iterator();
			}
		    }
		    public void remove() {
			lastit.remove();
		    }
		};
	}
	public boolean add(Map.Entry<K,V> me) {
	    return GenericMultiMap.this.add(me.getKey(), me.getValue());
	}
	public boolean remove(Object o) {
	    if (!(o instanceof Map.Entry)) return false;
	    Map.Entry me = (Map.Entry) o;
	    return GenericMultiMap.this.remove(me.getKey(), me.getValue());
	}
	// other methods for efficiency.
	public boolean contains(Object o) {
	    if (!(o instanceof Map.Entry)) return false;
	    Map.Entry me = (Map.Entry) o;
	    return GenericMultiMap.this.contains(me.getKey(), me.getValue());
	}
	public void clear() {
	    GenericMultiMap.this.clear();
	}
    }
}
