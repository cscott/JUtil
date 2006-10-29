package net.cscott.jutil;

/**
 * {@link UnmodifiableMultiMapSet} prevents mutation of a wrapped
 * {@link MultiMapSet}.
 * @author C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: UnmodifiableMultiMapSet.java,v 1.1 2006-10-29 20:15:48 cananian Exp $
 */
public abstract class UnmodifiableMultiMapSet<K,V> extends UnmodifiableMapSet<K,V>
implements MultiMapSet<K,V> {
    protected UnmodifiableMultiMapSet() { }
    /** Implementations should return the wrapped {@link MultiMapSet} here. */ 
    @Override
    protected abstract MultiMapSet<K,V> wrapped();
    public UnmodifiableMultiMap<K,V> asMap() { return asMultiMap(); }
    public abstract UnmodifiableMultiMap<K,V> asMultiMap();
}
