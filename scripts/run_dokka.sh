#!/bin/bash

echo "Running spotless"
./gradlew clean spotlessApply
git add .
