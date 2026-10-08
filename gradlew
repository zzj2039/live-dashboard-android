#!/usr/bin/env sh

# Gradle wrapper script for live-dashboard-android

# Set up the Gradle environment
APP_HOME=$(dirname "$0")/..
APP_HOME=$(cd "$APP_HOME" && pwd)

# Use the wrapper if it exists
if [ -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
    exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
else
    # Fallback to system Gradle if wrapper is not available
    exec gradle "$@"
fi