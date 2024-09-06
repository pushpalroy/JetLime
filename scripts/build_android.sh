#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Create distributions folder in the root directory (if not already present)
mkdir -p distributions

# Build Android App
echo "Building Android App 📱"
./gradlew :sample:composeApp:assembleDebug

# Verify and copy the APK to the distributions folder
echo "Verifying Android App"
cp sample/composeApp/build/outputs/apk/debug/composeApp-debug.apk distributions/jetlime-sample-android.apk
