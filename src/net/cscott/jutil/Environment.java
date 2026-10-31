// Environment.java, created Sat Aug 28 22:12:08 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * An {@link Environment} is a {@link java.util.Map} with scoping:
 * you can save marks into the environment and undo all changes
 * since a mark.
 * <p>
 * Closing a mark undoes all changes since the mark, so a
 * try-with-resources statement makes a scope:
 * <pre>
 * try (Environment.Mark scope = env.getMark()) {
 *     env.put("x", 1); // the binding of x until the end of the block
 *     ...
 * } // x has its old binding, or none, again
 * </pre>
 * ({@code javac -Xlint:try} warns that {@code scope} is not used in
 * the block; Java 22 and later can name it {@code _} instead.)
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public interface Environment<K,V> extends java.util.Map<K,V> {
    /** A mark into an environment.  Closing it undoes all changes to
     *  its environment since the mark was taken. */
    public interface Mark extends AutoCloseable {
	/** Undoes all changes to this mark's environment since the mark
	 *  was taken, as {@link Environment#undoToMark(Mark)} does.
	 *  The marks of {@link HashEnvironment} and
	 *  {@link PersistentEnvironment} implement this; the default
	 *  implementation, for marks which do not know their
	 *  environment, throws {@link UnsupportedOperationException}.
	 *  @since 2.0 */
	@Override
	default void close() {
	    throw new UnsupportedOperationException
		("this mark does not know its environment: use undoToMark()");
	}
    }

    /** Get a mark that will allow you to restore the current state of
     *  this environment. */
    Mark getMark();
    /** Undo all changes since the supplied mark, restoring the map to
     *  its state at the time the mark was taken.  The undoToMark()
     *  operation must be repeatable.
     *  @throws IllegalArgumentException if the mark is not one of
     *  this environment's (in {@link HashEnvironment} and
     *  {@link PersistentEnvironment}; since 2.0) */
    void undoToMark(Mark m);
}
