package net.cscott.jutil;

/**
 * {@link UnmodifiableMultiMapSet} prevents mutation of a wrapped
 * {@link MultiMapSet}.
 * @author C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 * @since 1.3
 */
public abstract class UnmodifiableMultiMapSet<K,V> extends UnmodifiableMapSet<K,V>
implements MultiMapSet<K,V> {
    protected UnmodifiableMultiMapSet() { }
    /** Implementations should return the wrapped {@link MultiMapSet} here. */ 
    @Override
    protected abstract MultiMapSet<K,V> wrapped();
    @Override public UnmodifiableMultiMap<K,V> asMap() { return asMultiMap(); }
    @Override public abstract UnmodifiableMultiMap<K,V> asMultiMap();
}
