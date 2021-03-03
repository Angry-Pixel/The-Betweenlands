#!/bin/bash

# Runs the build

if [ "$BS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -k "$BS_DECR_KEY_SECRET" -in keystore.jks.enc -out keystore.jks -d
fi

chmod +x gradlew
./gradlew build

