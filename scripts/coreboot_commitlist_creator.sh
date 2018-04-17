#!/bin/bash

# We assume that the repository is set to desired HEAD commit as starting
# point for extracting the commits until the following date: 2010-03-01

COMMIT_LIMIT=20100301 # represents the committer date as number, which defines the point in time Coreboot completely switched to Kbuild

# Coreboot git repo dir
# commit list save dir

while getopts :i:o:h OPT; do
    case $OPT in
        i)
            REPOSITORY_HOME="$OPTARG" # Path to repository
            ;;
        o)
            OUTPUT_DIRECTORY="$OPTARG" # Path to output directory
            ;;
        h)
            exit
            ;;
    esac
done

# ++++++++++++++++++++++++++ #
# Go to repository directory #
echo "Switching to repository $REPOSITORY_HOME"
cd $REPOSITORY_HOME
# Go to repository directory #
# ++++++++++++++++++++++++++ #


# +++++++++++++++++++++++++++++++ #
# Get all commits from repository #
echo "Retrieving commits from repository $REPOSITORY_HOME"
commitList=$(git log --oneline | cut -d' ' -f1)
availableCommitsCounter=$(echo $commitList | wc -w)
echo -e "$availableCommitsCounter commits found\n"
# Get all commits from repository #
# +++++++++++++++++++++++++++++++ #

# ++++++++++++++++++++ #
# Extract commit diffs #
echo "Extracting commits"
extractedCommitsCounter=0
for commit in $commitList; do
    committerDate=$(git show -s --format=%ci $commit | cut -d' ' -f1 | tr -d -)
    if ((committerDate >= COMMIT_LIMIT)); then
        echo $commit >> "$OUTPUT_DIRECTORY/coreboot-commitlist"
        extractedCommitsCounter=$((extractedCommitsCounter + 1))
    fi
done
echo "$extractedCommitsCounter commits extracted"
