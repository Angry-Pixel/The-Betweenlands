#!/bin/bash

echo "Running deployment"
  
if [ "$BS_PULL_REQUEST" == 'false' ]; then

  RET_CODE=0
  if [ "$BS_ABORT_ON_DEPLOY_ERROR" == 'true' ]; then
    RET_CODE=1
  fi
  
  echo "::group::Authenticating"
  
  echo "Loading deployment SSH key"

  DEP_KEY_PATH=".github/workflows/${GITHUB_WORKFLOW}/depkey"
  
  echo "$BS_SSH_KEY_SECRET" > $DEP_KEY_PATH
  
  #Authenticate
  eval "$(ssh-agent -s)"
  chmod 600 $DEP_KEY_PATH
  ssh-add $DEP_KEY_PATH
  
  rm $DEP_KEY_PATH
  
  if [ -f $DEP_KEY_PATH ]; then
    echo "Deployment SSH key could not be deleted"
	return $RET_CODE
  fi
  
  echo "::endgroup::"
  
  DEPLOYMENT_PATH=".github/workflows/${GITHUB_WORKFLOW}/deployment"
  
  mkdir $DEPLOYMENT_PATH || return $RET_CODE
  
  mv "$BS_BUILD_NOTES_FILE" "${DEPLOYMENT_PATH}/build_notes"
  
  cd $DEPLOYMENT_PATH || return $RET_CODE
  
  #Clone deployment repo
  echo "::group::Cloning deployment repository"
  
  git clone -b "${BS_DEPLOY_REPOSITORY_BRANCH}" --single-branch --depth 1 "$BS_DEPLOY_REPOSITORY_URI" repository
  
  if [ ! $? -eq 0 ]; then
    echo "Failed cloning deploy repository $BS_DEPLOY_REPOSITORY_URI"
    return $RET_CODE
  fi
  
  cd repository || return $RET_CODE
  
  echo "::endgroup::"
  
  echo "::group::Preparing deployment files"
  
  #Set up git for deployment
  git config --local user.name "$BS_DEPLOY_NAME"
  git config --local user.email "<>"
  git remote add deployment "$BS_DEPLOY_REPOSITORY_URI"
  git remote -v
  
  git rm -r '*' || return $RET_CODE
  
  cat <<EOT >> build_config
[number]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_NUMBER}")
[type]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_TYPE}")
[release]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_RELEASE}")
[title]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_TITLE}")
[tag]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_TAG}")
[url]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_URL}")
[branch]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_BRANCH}")
[commit]:
$(sed 's/\:/\\:/g' <<< "${BS_BUILD_COMMIT}")
EOT

  mv "../build_notes" "build_notes"

  mkdir -p ".github/workflows/publish" || return $RET_CODE
  cp -r "../../config" ".github/workflows/publish/config" || return $RET_CODE
  cp "../../build.sh" ".github/workflows/publish/build.sh" || return $RET_CODE
  cp "../../setup.sh" ".github/workflows/publish/setup.sh" || return $RET_CODE
  cp "../../publish.yml" ".github/workflows/publish.yml" || return $RET_CODE
  
  echo "::endgroup::"
  
  #Push to deployment repo
  
  echo "::group::Deploying"
  
  git add -A
  git status
  
  git commit -m "${BS_BUILD_TITLE}"
  
  if [ ! $? -eq 0 ]; then
    echo "Failed committing to deploy repository $BS_DEPLOY_REPOSITORY_URI"
    return $RET_CODE
  fi
  
  git push deployment "${BS_DEPLOY_REPOSITORY_BRANCH}"
  
  if [ ! $? -eq 0 ]; then
    echo "Failed pushing to deploy repository $BS_DEPLOY_REPOSITORY_URI"
    return $RET_CODE
  fi
  
  cd -
  
  echo "::endgroup::"

else

  echo "Skipping..."
  
fi