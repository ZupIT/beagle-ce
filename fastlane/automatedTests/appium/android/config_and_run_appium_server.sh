#!/bin/bash
set -e

sudo chown -R runner:runner /usr/local/
sudo npm install -g appium --unsafe-perm=true --allow-root
appium -v
appium &>/dev/null &