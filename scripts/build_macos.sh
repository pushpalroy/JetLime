#!/bin/bash

# Exit the script on any error
set -e

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Create distributions folder in the root directory (if not already present)
mkdir -p distributions/macos

# Build Mac Desktop App
echo "Building Mac Desktop App 🖥️"
./gradlew :sample:composeApp:packageUberJarForCurrentOS --console=plain --stacktrace

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Mac Desktop build successful."

    # Verify and copy the JAR to the distributions folder
    JAR_PATH="sample/composeApp/build/compose/jars/JetLime Samples-macos-arm64-1.0.0.jar"

    if [ -f "$JAR_PATH" ]; then
        cp "$JAR_PATH" distributions/macos/jetlime-sample-macos-x64.jar
        echo "Mac Desktop app copied to distributions/macos/jetlime-sample-macos-x64.jar"
    else
        echo "JAR not found at expected path: $JAR_PATH"
        exit 1
    fi
else
    echo "Mac Desktop build failed."
    exit 1
fi