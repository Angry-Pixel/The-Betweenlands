#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  
  openssl aes-256-cbc -k "$deployKeyEncryptionKey" -in deploy_key.pem.enc -out deploy_key.pem -d
  
fi