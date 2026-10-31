# JUtil history

## 1.5 (2026-10-30)

The last release of the 1.x line, which builds on JDK 1.5 through 8.
The jar runs on Java 1.5 and later.

* `Factories.treeMapFactory()`, and `treeSetFactory()` and
  `treeMapFactory()` variants which take a `Comparator`.
* `Factories.treeSetFactory()` now requires a `Comparable` element type.
* The javadoc and the distribution build on JDK 8: javadoc is run with
  `-Xdoclint:none` on JDK 8 and later.

## 1.4 (2009-02-14)

* The jar is binary compatible with JDK 1.5, and compiled with debug
  information.
* More assertions in `WorkSet`, which check that the hash codes of its
  elements don't change.
* Moved from CVS to git.

## 1.3 (2006-10-30)

* JDK 1.5 or later is required: the build now uses ant (or Eclipse)
  instead of autoconf and the JSR-14 prototype compiler, and the
  workarounds for its bugs are gone.
* `PersistentMultiMapFactory`, whose multimaps (and their value sets)
  can be cloned in constant time.  `PersistentSet` implements
  `Iterable`.
* Factories for `EnumSet`, `EnumMap`, `LinkedHashSet` and
  `LinkedHashMap`.
* `MultiMapFactory` no longer creates `GenericMultiMap`s; use the new
  `GenericMultiMapFactory`.  `AbstractMultiMap` was factored out of
  `GenericMultiMap`.
* `CollectionWrapper`, `SetWrapper` and `ListWrapper` are abstract, with
  a `wrapped()` method which subclasses can narrow.
* `UnmodifiableMap`, `UnmodifiableMapSet` and `UnmodifiableMultiMapSet`,
  which protect these types from mutation; `UnmodifiableMultiMap` is now
  safe too.
  Unlike `Collections.unmodifiableMap()`, the `entrySet()` of an
  `UnmodifiableMap` is an `UnmodifiableMapSet`.
* `UnmodifiableIterator.proxy()`.
* `BitSetFactory` constructors take a `Collection` instead of a `Set`.
* Deprecated `WorkSet` (use `java.util.LinkedHashSet`) and
  `ListFactory.singleton` (use `java.util.Collections.singletonList`).
* Fixed a `NullPointerException` in `containsAll()` of the sets made by
  `PersistentSetFactory`.
* `jutil-source.jar`, for Eclipse.

## 1.2 (2004-02-07)

Built, but not released publicly at the time.

* `EMPTY_MAP` and `EMPTY_MULTIMAP` are parameterized.
* Constant-time `clone()` for `PersistentMap` and `PersistentSet`.
* Constructors use wildcards instead of explicit type parameters.
* More precise return type for `LinearSet.clone()`.
* Documentation improvements.

## 1.1 (2004-01-24)

* Interfaces match the wildcard signatures of the standard library
  collection classes.  Requires the JSR-14 2.4 alpha 1 prototype
  compiler.
* Updated with the changes made to the FLEX sources since 1.0.
* `PersistentMapFactory` and `PersistentSetFactory`.
* Uses the new `for` loop.
* Fixed `PersistentTreeNode.putAll`.

## 1.0 (2003-04-26)

* Initial release: the generic collection classes of the [FLEX]
  compiler, packaged as a library.  Requires a JSR-14 prototype
  compiler.
* `java -jar jutil.jar` prints the library version.

[FLEX]: http://flex.cscott.net/
