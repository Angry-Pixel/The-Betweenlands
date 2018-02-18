#!/bin/bash

REPOSITORY_SSH_URI=git@github.com:Angry-Pixel/The-Betweenlands-Development-Builds.git

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  
  if [ ! -f deploy_key.pem ]; then
    echo "Deployment SSH key not found"
	return 0
  fi
  
  #Get commit message
  releaseDescription=$(git log -1 --pretty=%B)
  
  #Authenticate
  eval "$(ssh-agent -s)"
  chmod 600 deploy_key.pem
  ssh-add deploy_key.pem
  
  mkdir repo
  
  git clone --depth 1 $REPOSITORY_SSH_URI repo
  
  if [ ! $? -eq 0 ]; then
    echo "Failed cloning deploy repository ${REPOSITORY_SSH_URI}"
    return 0
  fi
  
  cd repo || return 0
  
  git config --local user.name "Travis Build Wizard"
  git config --local user.email "<>"
  git remote add deployment $REPOSITORY_SSH_URI
  git remote -v
  
  rm -f build
  
  if [[ "$TRAVIS_TAG" == *"release"* ]]; then
    releaseType="release"
	releaseTitle="Release Build ${TRAVIS_BRANCH}-${TRAVIS_BUILD_NUMBER}"
  else
    releaseType="development"
	releaseTitle="Development Build ${TRAVIS_BRANCH}-${TRAVIS_BUILD_NUMBER}"
  fi
  
  #$(sed 's/\:/\\:/g' <<< "${releaseDescription}")
  
  cat <<EOT >> build
[build number]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_BUILD_NUMBER}")
[type]:
$(sed 's/\:/\\:/g' <<< "${releaseType}")
[title]:
$(sed 's/\:/\\:/g' <<< "${releaseTitle}")
[description]:
https://github.com/Angry-Pixel/The-Betweenlands/commit/${TRAVIS_COMMIT}
[branch]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_BRANCH}")
[commit]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_COMMIT}")
EOT

  git add build
  git commit -m "${releaseTitle}"
  git push deployment master
  
  cd -
fi
