#!/usr/bin/bash

releaseVersion=$1
nextVersion=$2

echo "creating release $releaseVersion" \
  && mvn versions:set -DnewVersion=$releaseVersion \
  && git add pom.xml */pom.xml \
  && git commit -m "release $releaseVersion" \
  && git tag -a $releaseVersion -m "release $releaseVersion" \
  && echo "preparing for next release $nextVersion" \
  && mvn versions:set -DnewVersion=$nextVersion-SNAPSHOT \
  && git add pom.xml */pom.xml \
  && git commit -m "prepare for development of $nextVersion" \
  && echo "pushing changes" \
  && git push upstream master:master \
  && git push upstream $releaseVersion:$releaseVersion


