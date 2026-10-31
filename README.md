# JUtil

JUtil is a library of generic collection classes and utilities for
Java, which grew out of the [FLEX] compiler.  It has no dependencies.

Among its classes:

 * **Multimaps.**  A `MultiMap` maps each key to a collection of values;
   `GenericMultiMap` is the usual implementation, and an
   `InvertibleMultiMap` can also look up the keys of a value.
 * **Persistent collections.**  `PersistentMap` and `PersistentSet` are
   immutable maps and sets whose updates return a new collection in
   O(lg n) time, sharing structure with the old one.
   `PersistentMapFactory`, `PersistentSetFactory` and
   `PersistentMultiMapFactory` make `java.util` collections backed by
   them, which can be cloned in constant time.
 * **Environments.**  An `Environment` is a map with scopes: closing an
   `Environment.Mark` undoes every change since the mark was made.
 * **Heaps.**  `BinaryHeap`, `BinomialHeap` and `FibonacciHeap`, with
   decrease-key, delete and union operations.
 * **Factories.**  `SetFactory`, `MapFactory` and the other factory
   interfaces let a class make collections of a type chosen by its
   caller; `Factories` has the standard ones, and a constructor
   reference such as `HashSet::new` is one too.  `AggregateSetFactory`,
   `AggregateMapFactory` and `BitSetFactory` make many small sets or
   maps with little space overhead.
 * **Others.**  `DisjointSet` (union-find), `IntervalTree`,
   `RedBlackTree`, `UniqueVector`, `LinearMap` and `LinearSet` (for small
   collections), `Pair`, and iterators such as `FilterIterator` and
   `CombineIterator`.

The [javadoc] describes every class.  See [HISTORY.md] for the changes in
each release; JUtil 2.0 deprecates the classes and methods which the JDK
now provides, and says what replaces each one.

## Requirements

JDK 21 or later is required to build and to run JUtil 2.x.  JUtil 1.5,
the last release of the 1.x line, builds on JDK 1.5 through 8 and runs
on Java 1.5 and later.

## Maven and Gradle

JUtil is published on Maven Central as `net.cscott:jutil`.  For Maven:
```xml
  <dependency>
    <groupId>net.cscott</groupId>
    <artifactId>jutil</artifactId>
    <version>2.0</version>
  </dependency>
```
For Gradle (in `build.gradle.kts`):
```kotlin
repositories { mavenCentral() }
dependencies {
    implementation("net.cscott:jutil:2.0")
}
```
The jar is an automatic module named `net.cscott.jutil`, for use from
the Java module system (`requires net.cscott.jutil;`).

## Building

JUtil builds with [Ant] (1.10.6 or later, with the `ant-junitlauncher`
task, which Debian and Ubuntu ship in `ant-optional`):

 * `ant` builds `jutil-<version>.jar`.
 * `ant javadoc` builds the documentation in `api/`, running the
   examples in it with [JDoctest].
 * `ant test` runs the JUnit 6 tests in `test/`, and reruns the
   examples which `ant javadoc` saved in `api/tests`.
 * `ant clean release` builds the release tarball, the jar, and a bundle
   for upload to Maven Central (signed with `gpg`; add
   `-Dgpg.keyname=...` to choose the key, or `-Dskip.sign=true` to skip
   signing).

`lib/` holds the jars which the tests need: JUnit's
`junit-platform-console-standalone`, JDoctest and Rhino.  None of them
is needed to use JUtil.

### Error Prone

`ant errorprone` checks the code with [Error Prone], which finds
common bugs that `javac` does not warn about.  It is optional, and its
jars are not in git.  Download them into `lib/` once, from Maven
Central, using the versions which `build.xml` names
(`errorprone.version` and `dataflow.version`):

    cd lib
    curl -fLO https://repo1.maven.org/maven2/com/google/errorprone/error_prone_core/2.51.0/error_prone_core-2.51.0-with-dependencies.jar
    curl -fLO https://repo1.maven.org/maven2/io/github/eisop/dataflow-errorprone/3.41.0-eisop1/dataflow-errorprone-3.41.0-eisop1.jar

A newer JDK may need a newer Error Prone.  To upgrade, change
`errorprone.version` in `build.xml`, and set `dataflow.version` to the
version of `dataflow-errorprone` which the new release's
[`error_prone_parent` POM][ep-pom] names as
`dataflow.version`.

## License

    JUtil, a library of generic collection classes.
    Copyright (C) 1998-2026 C. Scott Ananian <cscott@cscott.net>
    Portions copyright their authors; see the individual source files.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License, version 2, as
    published by the Free Software Foundation; see COPYING.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

[FLEX]:       http://flex.cscott.net/
[javadoc]:    https://cscott.net/Projects/JUtil/jutil-latest/doc
[HISTORY.md]: HISTORY.md
[Ant]:        https://ant.apache.org/
[JDoctest]:   https://github.com/cscott/JDoctest
[Error Prone]: https://errorprone.info/
[ep-pom]:     https://repo1.maven.org/maven2/com/google/errorprone/error_prone_parent/
