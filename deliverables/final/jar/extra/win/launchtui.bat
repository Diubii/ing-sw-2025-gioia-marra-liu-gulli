@echo off

::UTF-8
chcp 65001 > nul

set JARPATH=../..
set JARNAME=GalaxyTrucker-TUI.jar
"C:\Program Files\Java\jdk-23\bin\java.exe" ^ -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -jar %JARPATH%\%JARNAME%