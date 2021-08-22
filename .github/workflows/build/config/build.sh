#!/bin/bash

# Runs the build

if [ "$BS_PULL_REQUEST" == 'false' ]; then
  if [ "$BS_BUILD_RELEASE" == 'true' ]; then
    
	# Decrypt keystore
    openssl aes-256-cbc -md sha512 -pbkdf2 -iter 50000 -k "$BS_ENC_KEY_SECRET" -in keystore.jks.enc -out keystore.jks -d
    openssl aes-256-cbc -md sha512 -pbkdf2 -iter 50000 -k "$BS_ENC_KEY_SECRET" -in keystore_key.enc -out keystore_key -d
  
    # Set up keystore env variables
    export blKeyStore="${BS_BUILD_WORKSPACE}/keystore.jks"
    export blKeyStoreAlias=Angry-Pixel
    export blKeyStorePass=$(cat keystore_key)
    export blKeyStoreKeyPass=${blKeyStorePass}
    export blKeyStoreFingerprint=38:06:7D:68:78:81:1E:FB:38:B6:A0:45:52:1C:FD:80:B9:B6:0B:38

  fi
fi

chmod +x gradlew
./gradlew build

