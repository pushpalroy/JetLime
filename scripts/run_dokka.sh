#!/bin/bash

# Navigate to the root directory of the project
cd "$(dirname "$0")/.." || exit

echo "Running Dokka V2 HTML generation + sync"
./gradlew :jetlime:syncDokkaToDocs --no-configuration-cache "$@" || {
  echo "Dokka generation failed. Run with --stacktrace for details." >&2
  exit 1
}
