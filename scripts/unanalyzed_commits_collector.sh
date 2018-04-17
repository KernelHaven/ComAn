#!/bin/bash

resultDir=$1
unanalyzedCommits=$(<"$resultDir/ComAn_Unanalyzed.txt")

summaryFile="$resultDir/ComAn_UnanalyzedSummary.txt"
for commitFile in $unanalyzedCommits; do
    echo -e "$commit\n" >> $summaryFile
    commitContent=$(<"$resultDir/$commitFile")
    echo -e "$commitContent\n" >> $summaryFile
done
