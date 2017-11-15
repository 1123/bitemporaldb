#!/bin/bash

set -e # exit on errors
set -u # exit on unbound variables

echo "Running tests for main project"
sbt test

