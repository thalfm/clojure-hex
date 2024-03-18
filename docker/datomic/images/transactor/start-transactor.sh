#!/bin/sh
set -e

echo -e "\n storage-admin-password=$STORAGE_ADMIN_PASSWORD" >> config/dev-transactor-template.properties
echo -e "\n storage-datomic-password=$STORAGE_DATOMIC_PASSWORD" >> config/dev-transactor-template.properties

chmod a+x bin/transactor
bin/transactor -Ddatomic.printConnectionInfo=true config/dev-transactor-template.properties