#!/bin/sh
#
# This script is used to run your program on CodeCrafters
#
# This runs after .codecrafters/compile.sh
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit on failure

cp target/build-your-own-shell.jar /tmp/codecrafters-build-dir/build-your-own-shell.jar
exec java -jar /tmp/codecrafters-build-dir/build-your-own-shell.jar

