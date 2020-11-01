#!/bin/bash

REPOSITORY_SSH_URI=git@github.com:Angry-Pixel/The-Betweenlands-Development-Builds.git

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  
  if [ ! -f deploy_key.pem ]; then
    echo "Deployment SSH key not found"
	return 0
  fi
  
  #Check release type (release or dev build)
  if [[ "$TRAVIS_TAG" == *"release"* ]]; then
    release_type="release"
	release_title="Release Build ${TRAVIS_BRANCH}-${TRAVIS_BUILD_NUMBER}"
  else
    release_type="development"
	release_title="Development Build ${TRAVIS_BRANCH}-${TRAVIS_BUILD_NUMBER}"
  fi
  
  #Create release notes
  rm -f release_notes

  echo "Commit: https://github.com/Angry-Pixel/The-Betweenlands/commit/${TRAVIS_COMMIT}" >> release_notes
  echo "" >> release_notes

  if [[ "$release_type" == "release" ]]; then
    #Get previous release tag and then list commits since that release as release notes
	git fetch --all --tags
    previous_release_tag=$(git describe --tags $(git rev-list --tags --max-count=1 --skip=1) --abbrev=0 --match *-release)
	echo "Creating list of changes since ${previous_release_tag}..."
    git log ${previous_release_tag}..HEAD --pretty=format:'%an, %ar (%ad):%n%B' --no-merges >> release_notes
  else
    #Use latest commit message as release note
    git log -1 --pretty=format:'%an, %ar (%ad):%n%B' >> release_notes
  fi
  
  #Authenticate
  eval "$(ssh-agent -s)"
  chmod 600 deploy_key.pem
  ssh-add deploy_key.pem
  
  #Clone dev build repo
  mkdir repo
  
  git clone --depth 1 $REPOSITORY_SSH_URI repo
  
  if [ ! $? -eq 0 ]; then
    echo "Failed cloning deploy repository ${REPOSITORY_SSH_URI}"
    return 0
  fi
  
  cd repo || return 0
  
  #Set up git for deployment
  git config --local user.name "Travis Build Wizard"
  git config --local user.email "<>"
  git remote add deployment $REPOSITORY_SSH_URI
  git remote -v
  
  #Create build infos
  rm -f build
  
  cat <<EOT >> build
[build number]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_BUILD_NUMBER}")
[type]:
$(sed 's/\:/\\:/g' <<< "${release_type}")
[title]:
$(sed 's/\:/\\:/g' <<< "${release_title}")
[branch]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_BRANCH}")
[commit]:
$(sed 's/\:/\\:/g' <<< "${TRAVIS_COMMIT}")
EOT

  #Move release notes to dev build repo
  rm -f release_notes
  mv ../release_notes release_notes

  #Push to dev build repo
  git add release_notes
  git add build
  git commit -m "${release_title}"
  git push deployment master
  
  cd -
fi
