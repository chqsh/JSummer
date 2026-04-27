#!/bin/bash

# $Id: rescompile.sh,v 1.2 2006-03-24 10:47:57 io Exp $
# (c) 2005 - 2006 Klaus Zerwes zero-sys.net
# 
# This package is free software.
# This software is licensed under the terms of the 
# GNU General Public License (GPL), version 2.0 or later, 
# as published by the Free Software Foundation. 
# See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
# latest version of the GNU General Public License.

if [ "$1" == "" ]; then
    echo "missing compiler";
    exit 1;
fi


if [ "$2" == "" ]; then
    echo "missing resourcefile";
    exit 1;
fi

if [ "$3" == "" ]; then
    echo "missing resourceoutputfile";
    exit 1;
fi

#pwd
echo "resourcecompile $1: "$2" -> "$3".o";

# ATENTION: strange things happen
# resources ar seeked in com/de/zerosys/JSummer/
$1 -c --resource "com/de/zerosys/JSummer/$2" -o $3.o $2
