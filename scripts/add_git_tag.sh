#!/bin/bash

# Script to add annotated tag with version number to the main branch
# This script should be executed with the correct version number after every release to MavenCentral
TAG="3.0.1"
COMMENT="Release $TAG"
BRANCH="main"

# Fetch the latest changes in the repository
git fetch

# Check if the main branch exists and switch to it
if git rev-parse --verify $BRANCH; then
    git checkout $BRANCH
    git pull origin $BRANCH
else
    echo "Branch '$BRANCH' does not exist."
    exit 1
fi

# Add the annotated tag
git tag -a $TAG -m "$COMMENT"

# Push the tag to remote repository
git push origin $TAG