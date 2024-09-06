#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Build Web App
echo "Building Web App 🌎"
./gradlew :sample:composeApp:wasmJsBrowserDistribution

# Create the distributions/jetlime-web folder in the root directory
mkdir -p distributions/jetlime-web

# Copy the production executable to the distributions folder
cp -r sample/composeApp/build/dist/wasmJs/productionExecutable/ distributions/jetlime-web/

echo "Web app build and copied to distributions/jetlime-web"
