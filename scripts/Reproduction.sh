#!/bin/bash
 
LINUX_KERNEL_REPO_URL="https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git"
LINUX_KERNEL_IC_SHA="1da177"
LINUX_KERNEL_LC_SHA="d528ae0"

COREBOOT_REPO_URL="https://review.coreboot.org/coreboot.git"
COREBOOT_IC_SHA="75bf053"
COREBOOT_LC_SHA="7504268"
COREBOOT_COMMITLIST_CREATOR="$PWD/coreboot_commitlist_creator.sh"

COMMIT_EXTRACTION_SCRIPT="$PWD/ComEx.sh"
COMMIT_ANALYSIS_TOOL="$PWD/ComAn.jar"
COMMIT_VISUALIZATION_SCRIPT="$PWD/ComVi.R"
UNANALYZED_COMMITS_COLLECTOR_SCRIPT="$PWD/unanalyzed_commits_collector.sh"
FILE_COUNTER_SCRIPT="$PWD/file_counter.sh"

showHelp() {
    echo -e "\n\tReproduction\n\n"
    echo "The Reproduction.sh script calls the different tools and scripts of this"
    echo "archive to provide exactly the same results as presented in our technical"
    echo "report [1]. Further, this script also checks the availability of the required"
    echo "software (see \"Overview\" in readme.txt) to execute the tools and scripts. If"
    echo "some of the requirements are not satisfied, the script informs about the missing"
    echo "software and shows an example of how we installed that software. The"
    echo "reproduction requires all tools and scripts of this archive to be located in"
    echo -e "the same directory as this script.\n"
    echo "[1] Christian Kröher and Klaus Schmid. A Commit-Based Analysis of Software"
    echo "    Product Line Evolution: Two Case Studies. Report No. 2/2017, SSE 2/17/E,"
    echo -e "    2017\n\n"
    echo "Usage: ${0##*/} [-t SPL] [-r DIR] [-o DIR]"
    echo " -t [Ll]inux || [Cc]oreboot  specify the target software product line for"
    echo "                             which the analysis should be reproduced"
    echo " -r <repodir>                specify the directory to clone the target"
    echo "                             repository to"
    echo " -o <outputdir>              specify the directory to save the output of the"
    echo "                             entire analysis to"
    echo " -h                          show this message"
}

while getopts :h:t:r:o:h OPT; do
    case $OPT in
        h)
            showHelp
            exit
            ;;
        t)
            TARGET_REPO="$OPTARG" # Which repository to analyze (Linux, Coreboot)
            ;;
        r)
            REPOSITORY_HOME="$OPTARG" # Path to clone repository to
            ;;
        o)
            OUTPUT_DIRECTORY="$OPTARG" # Path to save results to
            ;;
    esac
done


countFiles(){
    # Parameter $1 defines state of repository (initial or latest commit)
    echo "Counting files in $REPOSITORY_HOME..."
    bash $FILE_COUNTER_SCRIPT $REPOSITORY_HOME >> "$OUTPUT_DIRECTORY/${1}_filecount"
}

# Check if target repository is specified
if [ ! "$TARGET_REPO" == "Linux" ] && [ ! "$TARGET_REPO" == "linux" ] && [ ! "$TARGET_REPO" == "Coreboot" ] && [ ! "$TARGET_REPO" == "coreboot" ]; then
    echo "[ERROR] Missing target repository!"
    echo "        The given value for option '-t' does not match"
    echo -e "        any of the expected values '[Ll]inux' or '[Cc]oreboot'\n"
    showHelp
    exit    
fi

# Check if path to clone repository to is specified
if [ -z "$REPOSITORY_HOME" ]; then
    echo "[ERROR] Missing path to clone repository to!"
    echo -e "        No path for option '-r' specified\n"
    showHelp
    exit
else
    if [ ! -e "$REPOSITORY_HOME" ] || [ ! -d "$REPOSITORY_HOME" ]; then
        echo "[ERROR] Missing path to clone repository to!"
        echo "        The given path for option '-r' does not exist"
        echo -e "        or is not a directory\n"
        showHelp
        exit
    fi
fi

# Check if path to save results to is specified
if [ -z "$OUTPUT_DIRECTORY" ]; then
    echo "[ERROR] Missing path to save output to!"
    echo -e "        No path for option '-o' specified\n"
    showHelp
    exit
else
    if [ ! -e "$OUTPUT_DIRECTORY" ] || [ ! -d "$OUTPUT_DIRECTORY" ]; then
        echo "[ERROR] Missing path to save output to!"
        echo "        The given path for option '-o' does not exist"
        echo -e "        or is not a directory\n"
        showHelp
        exit
    fi
fi

# Check for Git installation
if ! hash git 2>/dev/null; then
    echo "[ERROR] Missing Git installation!"
    echo -e "        Install Git, e.g. 'sudo apt-get install git'\n"
    exit
fi

# Check for Java installation
if ! hash java 2>/dev/null; then
    echo "[ERROR] Missing Java installation!"
    echo -e "        Install Java, e.g. 'sudo apt-get install openjdk-7-jre'\n"
    exit
fi

# Check for R installation
if ! hash Rscript 2>/dev/null; then
    echo "[ERROR] Missing R installation!"
    echo "        Install R, e.g. like this:"
    echo "        1. 'sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80"
    echo "           '    --recv-keys E084DAB9'"
    echo "        2. 'sudo cp /etc/apt/sources.list /etc/apt/sources.list.bak'"
    echo "        3. 'echo \"deb http://cran.r-project.org/bin/linux/ubuntu trusty/\""
    echo "           '    | sudo tee -a /etc/apt/sources.list'"
    echo "        4. 'sudo apt-get update'"
    echo "        5. 'sudo apt-get install r-base r-base-dev'"
    echo "        6. 'sudo -i R'"
    echo "        7. 'install.packages('Hmisc')'"
    echo "        8. 'install.packages('nortest')'"
    echo -e "        8. 'quit()'\n"
    exit
else
    # R is installed, check for required packages
    installedPackages=$(R -q -e "installed.packages()[,1]")
    if [[ ! $installedPackages == *"Hmisc"* ]] && [[ ! $installedPackages == *"nortest"* ]]; then
        echo "[ERROR] Missing R packages!"
        echo "        Install R packages, e.g. like this:"
        echo "        1. 'sudo -i R'"
        echo "        2. 'install.packages('Hmisc')'"
        echo "        3. 'install.packages('nortest')'"
        echo -e "        4. 'quit()'\n"
        exit
    fi
fi

# Check if ComEx.sh script is available
if [ ! -e "$COMMIT_EXTRACTION_SCRIPT" ]; then
    echo "[ERROR] Missing ComEx.sh script!"
    echo "        This script should be located at the same place as the"
    echo "        Reproduction.sh script"
    echo "        In this case at '$COMMIT_EXTRACTION_SCRIPT'"
    echo "        Start Reproduction.sh by first navigating to it's location"
    echo -e "        and then executing it via 'bash Reproduction.sh <params>'\n"
    exit
fi

# Check if ComAn.jar tool is available
if [ ! -e "$COMMIT_ANALYSIS_TOOL" ]; then
    echo "[ERROR] Missing ComAn.jar tool!"
    echo "        This tool should be located at the same place as the"
    echo "        Reproduction.sh script"
    echo "        In this case at '$COMMIT_ANALYSIS_TOOL'"
    echo "        Start Reproduction.sh by first navigating to it's location"
    echo -e "        and then executing it via 'bash Reproduction.sh <params>'\n"
    exit
fi

# Check if *.R scripts are available
if [ ! -e "$COMMIT_VISUALIZATION_SCRIPT" ]; then
    echo "[ERROR] Missing ComVi.R script!"
    echo "        This script and all other *.R scripts should be"
    echo "        located at the same place as the Reproduction.sh"
    echo "        script"
    echo "        In this case at '$COMMIT_VISUALIZATION_SCRIPT'"
    echo "        Start Reproduction.sh by first navigating to it's location"
    echo -e "        and then executing it via 'bash Reproduction.sh <params>'\n"
    exit
fi

# Check if file_counter.sh script is available
if [ ! -e "$FILE_COUNTER_SCRIPT" ]; then
    echo "[ERROR] Missing file_counter.sh script!"
    echo "        This script should be located at the same place as the"
    echo "        Reproduction.sh script"
    echo "        In this case at '$FILE_COUNTER_SCRIPT'"
    echo "        Start Reproduction.sh by first navigating to it's location"
    echo -e "        and then executing it via 'bash Reproduction.sh <params>'\n"
    exit
fi

# 1. Clone desired repository
if [ "$TARGET_REPO" == "Coreboot" ] || [ "$TARGET_REPO" == "coreboot" ]; then
    echo "Cloning Coreboot repository..."
    git clone $COREBOOT_REPO_URL $REPOSITORY_HOME
    # 2.a.1 Move HEAD to latest Coreboot commit from technical report
    echo "Moving HEAD to latest Coreboot commit as described in report..."
    cd $REPOSITORY_HOME
    git checkout $COREBOOT_LC_SHA
    IC_SHA=$COREBOOT_IC_SHA
    # 2.a.2 Create Coreboot commit list as used in technical report
    echo "Creating Coreboot commit list containing the subset of commits as described in report..."
    bash $COREBOOT_COMMITLIST_CREATOR -i $REPOSITORY_HOME -o $OUTPUT_DIRECTORY
    # 2.a.3 Run ComEx.sh on repository with commit list
    echo "Running Commit Extraction (ComEx) script..."
    bash $COMMIT_EXTRACTION_SCRIPT -i $REPOSITORY_HOME -o $OUTPUT_DIRECTORY -l "$OUTPUT_DIRECTORY/coreboot-commitlist"
else
    # Should be Linux in all other cases; wrong targets will be caught before
    echo "Cloning Linux kernel repository..."
    git clone $LINUX_KERNEL_REPO_URL $REPOSITORY_HOME
    # 2.b.1 Move HEAD to latest Linux kernel commit from technical report
    echo "Moving HEAD to latest Linux kernel commit as described in report..."
    cd $REPOSITORY_HOME
    git checkout $LINUX_KERNEL_LC_SHA
    IC_SHA=$LINUX_KERNEL_IC_SHA
    # 2.b.2 Run ComEx.sh on repository
    echo "Running Commit Extraction (ComEx) script..."
    bash $COMMIT_EXTRACTION_SCRIPT -i $REPOSITORY_HOME -o $OUTPUT_DIRECTORY
fi

# 3. Run ComAn.jar on extracted commits
echo "Running Commit Analysis (ComAn) tool..."
java -jar $COMMIT_ANALYSIS_TOOL -i $OUTPUT_DIRECTORY -o $OUTPUT_DIRECTORY

# 4. Run ComVi.R and other *.R scripts on analysis results
echo "Running Commit Visualization (ComVi) scripts..."
Rscript $COMMIT_VISUALIZATION_SCRIPT "$OUTPUT_DIRECTORY/ComAn_Results.tsv" $OUTPUT_DIRECTORY $TARGET_REPO

# 5. Create the unanalyzed commit summary
echo "Running unanalyzed commits collector script..."
bash $UNANALYZED_COMMITS_COLLECTOR_SCRIPT $OUTPUT_DIRECTORY

# 6. Count number of files at initial and latest commit
countFiles "latestCommit" # We are at the latest commit currently
git checkout $IC_SHA 
countFiles "initialCommit"

echo -e "\n\nReproduction finished!\n"
