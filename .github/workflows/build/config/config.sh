#!/bin/bash

# Sets up the necessary environment variables

# Required

echo "BS_ENC_DEPLOY_KEY_FILE=deploy_key.pem.enc" >> $GITHUB_ENV
echo "BS_DEPLOY_REPOSITORY_URI=git@github.com:Angry-Pixel/The-Betweenlands-Development-Builds.git" >> $GITHUB_ENV
echo "BS_RELEASE_TYPE=release" >> $GITHUB_ENV
echo "BS_BRANCH_NAME=${GITHUB_REF##*/}" >> $GITHUB_ENV
echo "BS_DEPLOY_NAME=Build Wizard" >> $GITHUB_ENV
echo "BS_ARTIFACTS_PATTERN=Build Wizard" >> $GITHUB_ENV

# Optional

if [ "$BS_PULL_REQUEST" == 'false' ]; then
  echo "DEPLOY_ENV=true" >> $GITHUB_ENV
  echo "DEPLOY_BUILD_TYPE=release" >> $GITHUB_ENV
  echo "DEPLOY_BUILD_NUMBER=$GITHUB_RUN_NUMBER" >> $GITHUB_ENV
fi