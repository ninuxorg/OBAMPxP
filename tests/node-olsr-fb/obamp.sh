#!/bin/sh
JAVA=java
VM_ARGS='-server -ea'

$JAVA $VM_ARGS -cp OBAMPxP.jar:lib/log4j-1.2.14.jar CollabTool $*
