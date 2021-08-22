#!/bin/bash

# Sets up the environment variables

echo "Running setup"

export BS_DEPLOY_REPOSITORY_URI='git@github.com'
echo "BS_DEPLOY_REPOSITORY_URI=${BS_DEPLOY_REPOSITORY_URI}" >> $GITHUB_ENV

export BS_DEPLOY_REPOSITORY_BRANCH='master'
echo "BS_DEPLOY_REPOSITORY_BRANCH=${BS_DEPLOY_REPOSITORY_BRANCH}" >> $GITHUB_ENV

export BS_DEPLOY_NAME='GitHub Actions'
echo "BS_DEPLOY_NAME=${BS_DEPLOY_NAME}" >> $GITHUB_ENV

export BS_ABORT_ON_DEPLOY_ERROR='true'
echo "BS_ABORT_ON_DEPLOY_ERROR=${BS_ABORT_ON_DEPLOY_ERROR}" >> $GITHUB_ENV

if [ -f "build_config" ]; then

  echo "Loading build config"

  declare -A table
  while IFS='' read -r line || [[ -n "$line" ]]; do
    if [[ "$line" == "["* ]] && [[ "$line" == *"]:" ]]; then
      if [ ! -z "${section}" ]; then
        table["${section}"]=$(cat entry)
      fi
      >entry
      section=${line:1:${#line}-3}
    else
      line=$(sed 's/\\:/\:/g' <<< $line) #Unescape \: in lines
      echo "${line}" >> entry
    fi
  done < "build_config"
  if [ ! -z ${section} ]; then
    table["${section}"]=$(cat entry)
  fi
  rm entry

  export BS_BUILD_NUMBER=${table['number']}
  echo "BS_BUILD_NUMBER=${BS_BUILD_NUMBER}" >> $GITHUB_ENV

  export BS_BUILD_TYPE=${table['type']}
  echo "BS_BUILD_TYPE=${BS_BUILD_TYPE}" >> $GITHUB_ENV

  export BS_BUILD_RELEASE=${table['release']}
  echo "BS_BUILD_RELEASE=${BS_BUILD_RELEASE}" >> $GITHUB_ENV

  export BS_BUILD_TITLE=${table['title']}
  echo "BS_BUILD_TITLE=${BS_BUILD_TITLE}" >> $GITHUB_ENV

  export BS_BUILD_TAG=${table['tag']}
  echo "BS_BUILD_TAG=${BS_BUILD_TAG}" >> $GITHUB_ENV

  export BS_BUILD_URL=${table['url']}
  echo "BS_BUILD_URL=${BS_BUILD_URL}" >> $GITHUB_ENV

  export BS_BUILD_BRANCH=${table['branch']}
  echo "BS_BUILD_BRANCH=${BS_BUILD_BRANCH}" >> $GITHUB_ENV

  export BS_BUILD_COMMIT=${table['commit']}
  echo "BS_BUILD_COMMIT=${BS_BUILD_COMMIT}" >> $GITHUB_ENV

  export BS_BUILD_NOTES=$(cat build_notes)
  echo 'BS_BUILD_NOTES<<EOF' >> $GITHUB_ENV
  cat build_notes >> $GITHUB_ENV
  echo 'EOF' >> $GITHUB_ENV
  
  export BS_BUILD_WORKSPACE=${GITHUB_WORKSPACE}/repository
  echo "BS_BUILD_WORKSPACE=${BS_BUILD_WORKSPACE}" >> $GITHUB_ENV
  
else

  if [ "$BS_IS_DEPLOYMENT" == 'true' ]; then
    echo "Could not find build config"
    return 1
  fi

  export BS_BUILD_TYPE='release'
  echo "BS_BUILD_TYPE=${BS_BUILD_TYPE}" >> $GITHUB_ENV
  
  export BS_BUILD_RELEASE='true'
  echo "BS_BUILD_RELEASE=${BS_BUILD_RELEASE}" >> $GITHUB_ENV
  
  export BS_BUILD_TITLE="Build ${GITHUB_REF##*/}-${GITHUB_RUN_NUMBER}"
  echo "BS_BUILD_TITLE=${BS_BUILD_TITLE}" >> $GITHUB_ENV
  
  export BS_BUILD_TAG="${BS_BUILD_BRANCH}-${BS_BUILD_NUMBER}-$(date +'%d.%m.%Y')"
  echo "BS_BUILD_TAG=${BS_BUILD_TAG}" >> $GITHUB_ENV
  
  export BS_BUILD_NOTES_FILE='build_notes'
  echo "BS_BUILD_NOTES_FILE=${BS_BUILD_NOTES_FILE}" >> $GITHUB_ENV
  
  export BS_BUILD_NUMBER=${GITHUB_RUN_NUMBER}
  echo "BS_BUILD_NUMBER=${BS_BUILD_NUMBER}" >> $GITHUB_ENV
  
  export BS_BUILD_BRANCH=${GITHUB_REF##*/}
  echo "BS_BUILD_BRANCH=${BS_BUILD_BRANCH}" >> $GITHUB_ENV
  
  export BS_BUILD_URL=${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}
  echo "BS_BUILD_URL=${BS_BUILD_URL}" >> $GITHUB_ENV
  
  export BS_BUILD_COMMIT=${GITHUB_SHA}
  echo "BS_BUILD_COMMIT=${BS_BUILD_COMMIT}" >> $GITHUB_ENV
  
  export BS_BUILD_WORKSPACE=${GITHUB_WORKSPACE}
  echo "BS_BUILD_WORKSPACE=${BS_BUILD_WORKSPACE}" >> $GITHUB_ENV
  
fi

echo "::group::Executing config"

chmod +x "./.github/workflows/${GITHUB_WORKFLOW}/config/config.sh"
source "./.github/workflows/${GITHUB_WORKFLOW}/config/config.sh"

echo "::endgroup::"

if [ "$BS_BUILD_RELEASE" == 'true' ]; then
  echo "BS_BUILD_PRERELEASE=false" >> $GITHUB_ENV
else
  echo "BS_BUILD_PRERELEASE=true" >> $GITHUB_ENV
fi