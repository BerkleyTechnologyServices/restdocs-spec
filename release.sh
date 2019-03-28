#!/usr/bin/env bash

echo "creating release" \
  && mvn -q build-helper:parse-version \
            versions:set -DnewVersion=0.\${parsedVersion.minorVersion} \
            versions:commit \
  && releaseVersion=$(mvn -q build-helper:parse-version help:evaluate -Dexpression=project.version -DforceStdout) \
  && git add pom.xml */pom.xml \
  && git commit -m "release $releaseVersion" \
  && git tag -a $releaseVersion -m "release $releaseVersion" \
  && mvn -l release.log clean deploy \
  && echo "release created: $releaseVersion" \
  && echo "preparing for next release" \
  && mvn -q build-helper:parse-version \
            versions:set -DnewVersion=0.\${parsedVersion.nextMinorVersion}-SNAPSHOT \
            versions:commit \
  && nextVersion=$(mvn -q build-helper:parse-version help:evaluate -Dexpression=project.version -DforceStdout | sed 's/-SNAPSHOT//') \
  && git add pom.xml */pom.xml \
  && git commit -m "prepare for development of $nextVersion" \
  && echo "ready for next release: $nextVersion" \
  && echo "pushing changes" \
  && git push upstream master:master \
  && git push upstream $releaseVersion:$releaseVersion


