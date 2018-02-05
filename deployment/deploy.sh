#!/bin/bash

REPOSITORY_SSH_URI=git@github.com:Angry-Pixel/The-Betweenlands-Development-Builds.git

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

  #Authenticate
  eval "$(ssh-agent -s)"
  chmod 600 deploy_key.pem
  ssh-add deploy_key.pem
  
  mkdir repo
  
  git clone --depth 1 $REPOSITORY_SSH_URI repo
  
  cd repo || return
  
  git config --local user.name "Travis Build Wizard"
  git config --local user.email "<>"
  git remote add deployment $REPOSITORY_SSH_URI
  git remote -v
  
  rm -f build.json
  
  if [[ "$TRAVIS_TAG" == *"release"* ]]; then
    releaseType="release"
	releaseTitle="Release Build ${TRAVIS_BUILD_NUMBER}"
  else
    releaseType="development"
	releaseTitle="Development Build ${TRAVIS_BUILD_NUMBER}"
  fi
  
  releaseDescription=$TRAVIS_COMMIT_MESSAGE
  releaseDescription=$(echo "$releaseDescription" | tr '"' "'")
  
  cat <<EOT >> build.json
{
	"build_number": ${TRAVIS_BUILD_NUMBER},
	"type": "${releaseType}",
	"title": "${releaseTitle}",
	"description": "${releaseDescription}",
	"branch": "${TRAVIS_BRANCH}",
	"commit": "${TRAVIS_COMMIT}"
}
EOT

  git add build.json
  git commit -m "${releaseTitle}"
  git push deployment master
  
  cd -
fi