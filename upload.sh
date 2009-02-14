#!/bin/bash
# Version number is kept in build-user.xml.  Change it there.
PACKAGE=jutil
VERSION=$(ant echo-version | fgrep "Current version is: " | sed -e 's/^.*Current version is: //')
# build prerequisites
ant clean version build javadoc jar dist
# make upload bundle
/bin/rm -rf ${PACKAGE}-${VERSION}
mkdir ${PACKAGE}-${VERSION}
tar -c api | tar -C ${PACKAGE}-${VERSION} -x
mv ${PACKAGE}-${VERSION}/api ${PACKAGE}-${VERSION}/doc
(date ; echo "Released $PACKAGE $VERSION" ; echo " " ; cat ChangeLog)\
                > ${PACKAGE}-${VERSION}/ChangeLog.txt
cp ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}-${VERSION}/
gunzip ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar.gz
gzip --rsyncable ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.tar
( cd ${PACKAGE}-${VERSION} && \
    ln -s ${PACKAGE}-${VERSION}.tar.gz ${PACKAGE}.tar.gz )
cp ${PACKAGE}.jar ${PACKAGE}-${VERSION}/${PACKAGE}-${VERSION}.jar
touch ${PACKAGE}-${VERSION}/VERSION_${VERSION}
# transfer to the distribution machine.
rsync -avyz --delete-after --copy-dest=jutil-1.3 --copy-dest=jutil-1.2 ${PACKAGE}-${VERSION} cscott.net:public_html/Projects/JUtil/

/bin/rm -rf ${PACKAGE}-${VERSION}
