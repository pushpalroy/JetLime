#!/bin/bash

# Exit the script on any error
set -e

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Create distributions folder in the root directory (if not already present)
mkdir -p distributions/android

# Build Android App
echo "Building Android App 📱"
./gradlew :sample:composeApp:assembleDebug --console=plain --stacktrace

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Android build successful."

    # Verify and copy the APK to the distributions folder
    APK_PATH="sample/composeApp/build/outputs/apk/debug/composeApp-debug.apk"

    if [ -f "$APK_PATH" ]; then
        cp "$APK_PATH" distributions/android/jetlime-sample-android.apk
        echo "Android APK copied to distributions/android/jetlime-sample-android.apk"
    else
        echo "APK not found at expected path: $APK_PATH"
        exit 1
    fi
else
    echo "Android build failed."
    exit 1
fi