@echo off
set JARPATH=../..
set JARNAME=GalaxyTrucker-GUI.jar
set FX=openjfx-win

start "" "C:\Program Files\Java\jdk-23\bin\javaw.exe" ^
    --module-path %FX%\javafx-controls;%FX%\javafx-fxml;%FX%\javafx-graphics;%FX%\javafx-base ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -jar %JARPATH%\%JARNAME%
exit
