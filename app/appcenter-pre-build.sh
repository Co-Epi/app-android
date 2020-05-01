#!/usr/bin/env bash
#
# App Center Pre-Build Template sourced from:
# https://github.com/microsoft/appcenter/blob/master/sample-build-scripts/xamarin/version-name/appcenter-pre-build.sh

echo "Running appcenter-pre-build.sh script"

if [ -z "$APPCENTER_BUILD_ID" ]
then
    echo "You need define APPCENTER_BUILD_ID variable or run in App Center"
    exit
fi

if [ -z "$APPCENTER_SOURCE_DIRECTORY" ]
then
    echo "You need define APPCENTER_SOURCE_DIRECTORY variable or run in App Center"
    exit
fi

BUILD_GRADLE_FILE=$APPCENTER_SOURCE_DIRECTORY/app/build.gradle

if [ -e "$BUILD_GRADLE_FILE" ]
then
    echo "Updating version code to $APPCENTER_BUILD_ID in $BUILD_GRADLE_FILE"
    sed -i '' 's/versionCode [0-9]*/versionCode '$APPCENTER_BUILD_ID'/' $BUILD_GRADLE_FILE
    
    echo "File versionCode content:"
    cat $BUILD_GRADLE_FILE | grep versionCode
else
    echo "File not found: $BUILD_GRADLE_FILE"
fi

echo "Done running appcenter-pre-build.sh script"

