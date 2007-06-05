#!/bin/sh
JAVA=java
VM_ARGS='-server -ea'

$JAVA $VM_ARGS -cp OBAMPxP.jar CollabTool $*
