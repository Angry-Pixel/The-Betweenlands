#!/bin/bash

echo "Running build"

if [ "$BS_IS_DEPLOYMENT" == 'true' ]; then
  
  echo "::group::Cloning build repository"
  
  git clone --depth=10 --branch="$BS_BUILD_BRANCH" "${BS_BUILD_URL}.git" repository
  cd repository || return 1
  
  git checkout "$BS_BUILD_COMMIT"
  
  echo "::endgroup::"
  
  echo "::group::Executing build"
  
  chmod +x "../.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"
  source "../.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"
  
  cd -
  
  echo "::endgroup::"
  
else

  echo "::group::Executing build"

  chmod +x "./.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"
  source "./.github/workflows/${GITHUB_WORKFLOW}/config/build.sh"
  
  echo "::endgroup::"
  
fi