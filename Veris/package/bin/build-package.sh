#!/bin/bash

set -e

PACKAGER=${1}
INSTALLER_TYPE=${2}
MODULE_PATH=${3}
INPUT=${4}
OUTPUT=${5}
JAR=${6}
VERSION=${7}
APP_ICON=${8}
EXTRA_BUNDLER_ARGUMENTS=${9}

echo "launcher: ${EXTRA_BUNDLER_ARGUMENTS}"

${PACKAGER} \
  create-installer ${INSTALLER_TYPE} \
  --module-path ${MODULE_PATH} \
  --verbose \
  --echo-mode \
  --add-modules java.base,java.datatransfer,java.desktop,java.scripting,java.xml,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,javafx.controls,javafx.fxml,javafx.web,javafx.media,java.naming,java.sql,jdk.charsets \
  --input "${INPUT}" \
  --output "${OUTPUT}" \
  --name Verisimilitude \
  --main-jar ${JAR} \
  --version ${VERSION} \
  --jvm-args '--add-opens javafx.base/com.sun.javafx.reflect=ALL-UNNAMED' \
  --icon $APP_ICON \
  $EXTRA_BUNDLER_ARGUMENTS \
  --class com.verisjudge.Main

