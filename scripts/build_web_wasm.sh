#!/bin/bash

# Exit the script on any error
set -e

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Build Web WASM App
echo "Building Web WASM App 🌎"
./gradlew :sample:composeApp:wasmJsBrowserDistribution --console=plain --stacktrace

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Web WASM build successful."

    # Create the distributions/jetlime-web folder in the root directory
    mkdir -p distributions/web-wasm

    # Path to the production executable
    WEB_EXECUTABLE_PATH="sample/composeApp/build/dist/wasmJs/productionExecutable/"

    # Verify and copy the production executable to the distributions folder
    if [ -d "$WEB_EXECUTABLE_PATH" ]; then
        cp -r "$WEB_EXECUTABLE_PATH" distributions/web-wasm/
        echo "Web WASM app copied to distributions/web-wasm"
    else
        echo "Web WASM build output not found at expected path: $WEB_EXECUTABLE_PATH"
        exit 1
    fi
else
    echo "Web WASM build failed."
    exit 1
fi