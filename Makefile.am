## Process this file with automake to produce Makefile.in
MAINTAINERCLEANFILES = Makefile.in configure aclocal.m4 \
		       scripts/config.guess scripts/config.sub \
		       scripts/install-sh scripts/ltmain.sh \
		       scripts/missing scripts/mkinstalldirs
EXTRA_DIST = autogen.sh classes/README

SUBDIRS = src

pkgdata_DATA = @PACKAGE_TARNAME@.jar

# used to be $(shell find classes -name "*.class") 
@PACKAGE_TARNAME@.jar: $(shell find classes -type f)
	echo "Manifest-Version: 1.0" > manifest
#	echo "Main-Class: net.cscott.gjdoc.Main" >> manifest
#	echo "Class-Path: lib/jlfgr-1_0.jar lib/gtk-icons.jar" >> manifest
	cd classes && $(JAR) -cfm ../$@ ../manifest net
	-$(RM) manifest

run:
	$(MAKE)
	java -ea -jar @PACKAGE_TARNAME@.jar

# doesn't work because javadoc doesn't support GJ yet.
javadoc:
	$(RM) -rf doc doc-link
	mkdir -p doc doc-link/net/cscott/jutil
	cd doc-link/net/cscott/jutil
#	for f in SUBDIRS; do ln -s ../../../../src/$$f . ; done
	javadoc -version -author -breakiterator -source 1.5 \
	   -doctitle "JUtil documentation" \
	   -link http://java.sun.com/j2se/1.4/docs/api \
	   -d doc -sourcepath doc-link \
	   net.cscott.jutil
	$(RM) -rf doc-link

clean: more-cleaning
more-cleaning:
	-$(RM) -rf classes/net

list:
	@find . -name "*.java" -print

update: needs-cvs
	cvs -q update -PdA
ChangeLog:
	if [ -d CVS ]; then \
	  $(RM) $@; \
	  rcs2log > $@ ; \
	fi
dist: ChangeLog

backup: dist
	scp $(PACKAGE)-$(VERSION).tar.gz catfish.lcs.mit.edu:

# the 'cvs' rules only make sense if you've got a copy checked out from CVS
needs-cvs:
	@if [ ! -d CVS ]; then \
	  echo This rule needs CVS access to the source tree. ; \
	  exit 1; \
	fi

.PHONY: list update needs-cvs ChangeLog
