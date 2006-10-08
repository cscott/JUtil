## Process this file with automake to produce Makefile.in
MAINTAINERCLEANFILES = Makefile.in configure aclocal.m4 \
		       scripts/config.guess scripts/config.sub \
		       scripts/install-sh scripts/ltmain.sh \
		       scripts/missing scripts/mkinstalldirs
EXTRA_DIST = autogen.sh classes/README

SUBDIRS = src

pkgdata_DATA = @PACKAGE_TARNAME@.jar @PACKAGE_TARNAME@-source.jar

# used to be $(shell find classes -name "*.class") 
@PACKAGE_TARNAME@.jar: $(shell find classes -type f)
	echo "Manifest-Version: 1.0" > manifest
#       print the library version number if executed.
	echo "Main-Class: net.cscott.jutil.Version" >> manifest
	echo "Implementation-Version: $(VERSION)" >> manifest
	cd classes && $(JAR) -cfm ../$@ ../manifest net
	-$(RM) manifest
@PACKAGE_TARNAME@-source.jar: $(shell find src -name "*.java")
	$(MAKE) doc-link
	cd doc-link && $(JAR) -cf ../$@ net
	$(RM) -rf doc-link

run:
	$(MAKE)
	java -ea -jar @PACKAGE_TARNAME@.jar

doc-link:
	$(RM) -rf doc-link
	mkdir -p doc doc-link/net/cscott/
	cd doc-link/net/cscott/ ; ln -s ../../../src jutil
doc:
	$(MAKE) doc-link
	$(RM) -rf doc
	javadoc -version -author -breakiterator -source 1.5 \
	   -windowtitle "@PACKAGE_STRING@ documentation" \
	   -doctitle "@PACKAGE_STRING@ documentation" \
	   -header "@PACKAGE_STRING@" \
	   -bottom "Copyright &copy; 2003 C. Scott Ananian" \
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
upload: dist doc ChangeLog @PACKAGE_TARNAME@.jar
	$(RM) -rf $(PACKAGE)-$(VERSION)
	mkdir $(PACKAGE)-$(VERSION)
	tar -c doc | tar -C $(PACKAGE)-$(VERSION) -x
	(date ; echo "Released @PACKAGE_STRING@" ; echo " " ; cat ChangeLog)\
		> $(PACKAGE)-$(VERSION)/ChangeLog.txt
	cp $(PACKAGE)-$(VERSION).tar.gz $(PACKAGE)-$(VERSION)/
	cd $(PACKAGE)-$(VERSION) && \
		ln -s $(PACKAGE)-$(VERSION).tar.gz $(PACKAGE).tar.gz
	cp @PACKAGE_TARNAME@.jar $(PACKAGE)-$(VERSION)/
	touch $(PACKAGE)-$(VERSION)/VERSION_$(VERSION)
	tar -c $(PACKAGE)-$(VERSION)/ | \
		ssh catfish.lcs.mit.edu "mkdir -p public_html/Projects/@PACKAGE_NAME@ && cd public_html/Projects/@PACKAGE_NAME@ && /bin/rm -rf $(PACKAGE)-$(VERSION) && tar -xv"
	$(RM) -rf $(PACKAGE)-$(VERSION)

# the 'cvs' rules only make sense if you've got a copy checked out from CVS
needs-cvs:
	@if [ ! -d CVS ]; then \
	  echo This rule needs CVS access to the source tree. ; \
	  exit 1; \
	fi

.PHONY: list update needs-cvs ChangeLog doc
