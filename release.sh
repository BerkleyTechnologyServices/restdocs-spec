#!/usr/bin/env bash

echo "creating release" \
  && ./mvnw -q build-helper:parse-version \
            versions:set -DnewVersion=0.\${parsedVersion.minorVersion} \
            versions:commit \
  && releaseVersion=$(./mvnw -q build-helper:parse-version help:evaluate -Dexpression=project.version -DforceStdout) \
  && git add pom.xml */pom.xml \
  && git commit -m "release $releaseVersion" \
  && git tag -a $releaseVersion -m "release $releaseVersion" \
  && ./mvnw -l release.log clean deploy \
  && echo "release created: $releaseVersion" \
  && echo "preparing for next release" \
  && ./mvnw -q build-helper:parse-version \
            versions:set -DnewVersion=0.\${parsedVersion.nextMinorVersion}-SNAPSHOT \
            versions:commit \
  && nextVersion=$(./mvnw -q build-helper:parse-version help:evaluate -Dexpression=project.version -DforceStdout | sed 's/-SNAPSHOT//') \
  && git add pom.xml */pom.xml \
  && git commit -m "prepare for development of $nextVersion" \
  && echo "ready for next release: $nextVersion" \
  && echo "pushing changes" \
  && git push upstream master:master \
  && git push upstream $releaseVersion:$releaseVersion


