#!/bin/bash -x
echo 'Updating the test files like .jars, libs, ...'
cp ../dist/OBAMPxP.jar node-olsr-fb/
cp ../dist/obamp.sh node-olsr-fb/
chmod 755 node-olsr-fb/obamp.sh
mkdir -p node-olsr-fb/lib/ node-olsr-fb/image/
cp ../log4j.properties node-olsr-fb/
cp -R ../lib/* node-olsr-fb/lib/
cp -R ../image/* node-olsr-fb/image/
