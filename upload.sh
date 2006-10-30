#!/bin/bash
# before release, change version here, as well as in build-user.xml and in
# src/net/cscott/jutil/Version.java
PACKAGE=jutil
VERSION=1.3
# build prerequisites
ant clean build javadoc jar dist
# make upload bundle
/bin/rm -rf ${PACKAGE}-${VERSION}
mkdir ${PACKAGE}-${VERSION}
tar -c api | tar -C ${PACKAGE}-${VERSION} -x
mv ${PACKAGE}-${VERSION}/api ${PACKAGE}-${VERSION}/doc
(date ; echo "Released $PACKAGE $VERSION" ; echo " " ; cat ChangeLog)\
                > ${PACKAGE}-${VERSION}/ChangeLog.txt
cp ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/
( cd ${PACKAGE}-${VERSION} && \
    ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}.tar.gz )
cp ${PACKAGE}.jar ${PACKAGE}-${VERSION}/
touch ${PACKAGE}-${VERSION}/VERSION_${VERSION}
tar -c ${PACKAGE}-${VERSION}/ | \
    ssh k2.csail.mit.edu "mkdir -p public_html/Projects/JUtil && cd public_html/Projects/JUtil && /bin/rm -rf ${PACKAGE}-${VERSION} && tar -xv"
/bin/rm -rf ${PACKAGE}-${VERSION}
