#!/bin/bash

echo "Running build"

chmod +x "./.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"
"./.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"