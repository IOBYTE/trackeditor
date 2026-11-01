#!/usr/bin/env bash

rm -rf build
mkdir build
cd build
cmake ..
cmake --build .
cd bin
cp csr-trackeditor-fat.jar ../../trackeditor.jar
cd ../..
