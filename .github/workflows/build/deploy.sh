#!/bin/bash

echo "Running deployment"

if [ "$BS_PULL_REQUEST" == 'false' ]; then

  openssl aes-256-cbc -k "$BS_DECR_KEY_SECRET" -in "$BS_ENC_DEPLOY_KEY_FILE" -out ".github/workflows/${GITHUB_WORKFLOW}/deployment/depkey.pem" -d
  
  cd ".github/workflows/${GITHUB_WORKFLOW}/deployment" || return 0
  
  if [ ! -f "depkey.pem" ]; then
    echo "Deployment SSH key not found"
	return 0
  fi
  
  #Authenticate
  eval "$(ssh-agent -s)"
  chmod 600 depkey.pem
  ssh-add depkey.pem
  
  if [[ "$BS_RELEASE_TYPE" == "release" ]]; then
	RELEASE_TITLE="Release Build ${BS_BRANCH_NAME}-${GITHUB_RUN_NUMBER}"
  else
	RELEASE_TITLE="Development Build ${BS_BRANCH_NAME}-${GITHUB_RUN_NUMBER}"
  fi
  
  #Clone dev build repo
  mkdir "$GITHUB_REPOSITORY" || return 0
  
  git clone --depth 1 "$BS_DEPLOY_REPOSITORY_URI" repository
  
  if [ ! $? -eq 0 ]; then
    echo "Failed cloning deploy repository $BS_DEPLOY_REPOSITORY_URI"
    return 0
  fi
  
  cd repository || return 0
  
  rm -r *
  
  mkdir artifacts || return 0
  
  #Move artifacts
  echo "Preparing artifacts"
  find "$GITHUB_WORKSPACE" -type f | grep -i "$BS_ARTIFACTS_PATTERN" | xargs -i mv {} . | printf "%f\n"
  
  #Set up git for deployment
  git config --local user.name "$BS_DEPLOY_NAME"
  git config --local user.email "<>"
  git remote add deployment "$BS_DEPLOY_REPOSITORY_URI"
  git remote -v
  
  cat <<EOT >> build
[build number]:
$(sed 's/\:/\\:/g' <<< "${GITHUB_RUN_NUMBER}")
[type]:
$(sed 's/\:/\\:/g' <<< "${BS_RELEASE_TYPE}")
[title]:
$(sed 's/\:/\\:/g' <<< "${RELEASE_TITLE}")
[branch]:
$(sed 's/\:/\\:/g' <<< "${BS_BRANCH_NAME}")
[commit]:
$(sed 's/\:/\\:/g' <<< "${GITHUB_SHA}")
EOT
  
fi
