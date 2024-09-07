#!/bin/bash

# Exit the script on any error
set -e

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Build Web App
echo "Building Web App 🌎"
./gradlew :sample:composeApp:wasmJsBrowserDistribution --console=plain --stacktrace

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Web build successful."

    # Create the distributions/jetlime-web folder in the root directory
    mkdir -p distributions/web

    # Path to the production executable
    WEB_EXECUTABLE_PATH="sample/composeApp/build/dist/wasmJs/productionExecutable/"

    # Verify and copy the production executable to the distributions folder
    if [ -d "$WEB_EXECUTABLE_PATH" ]; then
        cp -r "$WEB_EXECUTABLE_PATH" distributions/web/
        echo "Web app copied to distributions/web"
    else
        echo "Web build output not found at expected path: $WEB_EXECUTABLE_PATH"
        exit 1
    fi
else
    echo "Web build failed."
    exit 1
fi