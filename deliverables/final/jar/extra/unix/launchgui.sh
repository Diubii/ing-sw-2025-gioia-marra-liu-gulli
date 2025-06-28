#!/bin/bash

# Per impostare UTF-8
export LANG=it_IT.UTF-8
export LC_ALL=it_IT.UTF-8

JARPATH="$(realpath "../..")"
JARNAME=GalaxyTrucker-GUI.jar
FX=openjfx-unix

java \
  --module-path "$FX/javafx-controls:$FX/javafx-fxml:$FX/javafx-graphics:$FX/javafx-base" \
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base \
  -Dfile.encoding=UTF-8 \
  -jar "$JARPATH/$JARNAME"
