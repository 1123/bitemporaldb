#!/bin/bash

set -e # exit on errors
set -u # exit on unbound variables

echo "Running tests for main project"
sbt test

echo "Installing mvn jar and pom.xml"
sbt publishM2

echo "Running usage test from java"
cd example-java && mvn test

