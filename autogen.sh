#!/bin/sh
#  build the automake/autoconf magic needed to build the rest of pcbmill.
##test -f include/stamp-h.in || touch include/stamp-h.in
test -f ChangeLog || touch ChangeLog
rm INSTALL COPYING scripts/{install-sh,mkinstalldirs,missing}

libtoolize --automake
aclocal-1.6
#autoheader
automake-1.6 --add-missing
autoconf2.50
test -f config.cache && rm -f config.cache
set +x

# reconfigure.  first try the autoconf 2.5 way
if test -f config.log && \
   sed -ne '5,5p' config.log | \
   grep "Invocation command line was" > /dev/null; then
    `sed -ne '7,7s/^ *\$ *//p'  config.log`
# then try the autoconf2.13 way:
elif test -f config.status && \
   sed -ne '4,4p' config.status | \
   grep "# This directory was configured as follows" > /dev/null; then
    `sed -ne '7,7s/^#//p'  config.status`
else
    echo Now run ./configure.
fi
