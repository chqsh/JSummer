@echo off
@setlocal

rem * $Id: rescompile.bat,v 1.5 2006-03-24 10:47:57 io Exp $
rem 
rem * (c) 2005 Klaus Zerwes zero-sys.net
rem * 
rem * This package is free software.
rem * This software is licensed under the terms of the 
rem * GNU General Public License (GPL), version 2.0 or later, 
rem * as published by the Free Software Foundation. 
rem * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
rem * latest version of the GNU General Public License.


set MISSING=

if x%1==x set MISSING=%MISSING%:compiler
if x%2==x set MISSING=%MISSING%:resourcefile
if x%2==x set MISSING=%MISSING%:resourceoutputfile

if NOT "%MISSING%"=="" GOTO MISSINGELEM

rem echo "%1 -c --resource com/de/zerosys/JSummer/%2 -o %3.o %2"
rem echoargs %1 -c --resource com/de/zerosys/JSummer/%2 -o %3.o %2

echo resourcecompile %1: %2 -> %3.o

%1 -c --resource com/de/zerosys/JSummer/%2 -o %3.o %2


GOTO END

:MISSINGELEM
echo missing element %MISSING%
GOTO ERROR

:ERROR
rem exit 1

:END
@endlocal