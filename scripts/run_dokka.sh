#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

echo "Running dokkaHtml"
./gradlew dokkaHtml
