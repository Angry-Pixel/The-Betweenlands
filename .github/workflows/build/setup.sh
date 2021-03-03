#!/bin/bash

# Sets up the deployment environment variables

echo "Running setup"

chmod +x "./.github/workflows/${GITHUB_WORKFLOW}/config/config.sh"
"./.github/workflows/${GITHUB_WORKFLOW}/config/config.sh"