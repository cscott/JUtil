## Process this file with automake to produce Makefile.in
MAINTAINERCLEANFILES = Makefile.in
#SUBDIRS = 

AM_JAVACFLAGS = -g #-Xlint:unchecked
# JAVAROOT directory must exist!
JAVAROOT=$(top_builddir)/classes

DISABLED = \
PersistentMapFactory.java \
PersistentSetFactory.java \
TestMap.java \
TestSet.java \

noinst_JAVA = \
AbstractHeap.java \
AbstractMapEntry.java \
AggregateMapFactory.java \
AggregateSetFactory.java \
BinaryHeap.java \
BinaryTree.java \
BinomialHeap.java \
BitSetFactory.java \
BitString.java \
CollectionFactory.java \
CollectionWrapper.java \
CombineIterator.java \
Default.java \
DisjointSet.java \
Environment.java \
Factories.java \
FibonacciHeap.java \
FilterIterator.java \
GenericInvertibleMap.java \
GenericInvertibleMultiMap.java \
GenericMultiMap.java \
HashEnvironment.java \
Heap.java \
Indexer.java \
IntervalTree.java \
InvertibleMap.java \
InvertibleMultiMap.java \
IteratorEnumerator.java \
LinearMap.java \
LinearSet.java \
ListFactory.java \
ListWrapper.java \
MapFactory.java \
MapSet.java \
MapWrapper.java \
MultiMap.java \
MultiMapFactory.java \
MultiMapSet.java \
PairMapEntry.java \
PersistentEnvironment.java \
PersistentMap.java \
PersistentSet.java \
PersistentTreeNode.java \
RedBlackTree.java \
ReferenceUnique.java \
ReverseIterator.java \
ReverseListIterator.java \
SetFactory.java \
SetWrapper.java \
SnapshotIterator.java \
UniqueStack.java \
UniqueVector.java \
UnmodifiableIterator.java \
UnmodifiableListIterator.java \
UnmodifiableMultiMap.java \
Util.java \
Version.java \
WorkSet.java \
Version.java

# automake 1.6.3 is broken: it doesn't actually include java sources
# in the distribution.  The following line works around this.
EXTRA_DIST = $(noinst_JAVA)
