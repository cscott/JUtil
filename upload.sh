#!/bin/bash
# Uploads a release to the website.  The version number is kept in
# build.xml: change it there.
set -e
PACKAGE=jutil
VERSION=$(ant echo-version | grep "Current version of" | sed -e 's/^.*is: //')
[ -n "$VERSION" ] || { echo "No version found" >&2; exit 1; }
# build prerequisites
ant clean test dist
# make upload bundle
/bin/rm -rf ${PACKAGE}-${VERSION}
mkdir ${PACKAGE}-${VERSION}
tar -c --exclude=api/tests api | tar -C ${PACKAGE}-${VERSION} -x
mv ${PACKAGE}-${VERSION}/api ${PACKAGE}-${VERSION}/doc
cp HISTORY.md ${PACKAGE}-${VERSION}/
cp ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/
gunzip ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar.gz
gzip --rsyncable ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar
( cd ${PACKAGE}-${VERSION} && \
    ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}.tar.gz )
cp ${PACKAGE}-${VERSION}.jar ${PACKAGE}-${VERSION}/
touch ${PACKAGE}-${VERSION}/VERSION_${VERSION}
# transfer to the distribution machine.
rsync -avyz --delete-after --copy-dest=jutil-1.3 --copy-dest=jutil-1.2 ${PACKAGE}-${VERSION} cscott.net:public_html/Projects/JUtil/

/bin/rm -rf ${PACKAGE}-${VERSION}
