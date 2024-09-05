#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Create distributions folder in the root directory (if not already present)
mkdir -p distributions

# Build Mac Desktop App
echo "Building Mac Desktop App 🖥️"
./gradlew :sample:composeApp:packageUberJarForCurrentOS

# Verify and copy the JAR to the distributions folder
echo "Verifying Mac Desktop App"
cp "sample/composeApp/build/compose/jars/JetLime Samples-macos-arm64-1.0.0.jar" distributions/jetlime-sample-macos-x64.jar

echo "Mac Desktop app build and copied to distributions/jetlime-sample-macos-x64.jar"