#!/bin/bash

# Exit the script on any error
set -e

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Build Web JS App
echo "Building Web JS App 🌎"
./gradlew :sample:composeApp:jsBrowserDistribution --console=plain --stacktrace

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Web JS build successful."

    # Create the distributions/jetlime-web folder in the root directory
    mkdir -p distributions/web-js

    # Path to the production executable
    WEB_EXECUTABLE_PATH="sample/composeApp/build/dist/js/productionExecutable/"

    # Verify and copy the production executable to the distributions folder
    if [ -d "$WEB_EXECUTABLE_PATH" ]; then
        cp -r "$WEB_EXECUTABLE_PATH" distributions/web-js/
        echo "Web JS app copied to distributions/web-js"
    else
        echo "Web JS build output not found at expected path: $WEB_EXECUTABLE_PATH"
        exit 1
    fi
else
    echo "Web JS build failed."
    exit 1
fi