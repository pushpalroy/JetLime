#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

# Build iOS app using xcodebuild
xcodebuild build \
  -workspace sample/iosApp/iosApp.xcworkspace \
  -configuration Debug \
  -scheme iosApp \
  -sdk iphonesimulator \
  -verbose

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "iOS build successful."

    # Create distributions directory if it doesn't exist
    mkdir -p distributions/ios

    # Copy the generated iOS build products to the distributions/ directory
    BUILD_DIR=$(xcodebuild -workspace sample/iosApp/iosApp.xcworkspace \
                -scheme iosApp -configuration Debug -sdk iphonesimulator -showBuildSettings | grep -m1 " BUILT_PRODUCTS_DIR" | awk '{print $3}')

    if [ -d "$BUILD_DIR" ]; then
        cp -R "$BUILD_DIR"/* distributions/ios/
        echo "iOS build copied to distributions/ios/ directory."
    else
        echo "Build directory not found!"
        exit 1
    fi
else
    echo "iOS build failed."
    exit 1
fi
