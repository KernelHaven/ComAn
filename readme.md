# [Com]mit [An]alysis 

## Overview                                

This archive contains the entire tool-set for extracting and analyzing commits
of git-based repositories hosting C-preprocessor- and Kbuild-based Software
Product Lines (SPLs). In general, the [Com]mit [Ex]traction process (realized
as ComEx.sh script) creates a "diff file" for each commit of the repository and
writes the changes introduced by the respective commit (the "git diff
information") to that file. The [Com]mit [An]alysis process (realized as
ComAn.jar Java-tool) differentiates these changes for each commit into
artifact-specific and variability information for different types of artifacts.
A detailed description of the tools realizing these processes can be found
below. The current versions of these two core tools support:

- ComEx: any git-based software repository
- ComAn: the Linux kernel and the Coreboot firmware SPLs; analyses of any
      other SPLs may lead to incorrect results due to differences in
      implementing variability information, presents of other file types, etc.

Besides the two core tools for the commit extraction and analysis, this archive
contains a set of additional scripts:
- A set of *.R scripts for [Com]mit [Vi]sualization based on the results
      produced by the ComAn.jar tool; a detailed description can be found below
- The coreboot_commitlist_creator.sh script for creating a custom list of
      the commits to be extracted and analyzed in case these processes are
      performed for the Coreboot firmware SPL; the resulting commit list only
      contains commits within a certain timeframe, which excludes changes at a
      time Coreboot was using a different (not supported) variability
      realization technique
- The file_counter.sh script for counting the number of different artifact
      types (this may summarize different file types, e.g. *.c, *.h, *.S files
      as code artifacts) available in the current version of a SPL
- The unanalyzed_commits_collector.sh script for summarizing the
      information of all extracted, but not analyzed commits into a single text
      file; the ComAn.jar tool may not analyze commits, which, for example, do
      not introduce changes to file contents, but only change the file
      permissions
- The Reproduction.sh script, which setups the analysis environment (e.g.
      cloning the respective SPL repositories) and calls the different tools
      and scripts to provide exactly the same results as presented in our
      technical report [1]; a detailed description can be found below

Technical Requirements:
- We suggest to use Ubuntu as operating system; we used Ubuntu 14.04.3 LTS
- For executing the ComEx.sh and the coreboot_commitlist_creator.sh
      scripts, git has to be installed; we used git version 1.9.1
- For executing the ComAn.jar Java-tool, Java has to be installed; we used:
    - java version "1.7.0_131"
    - OpenJDK Runtime Environment (IcedTea 2.6.9)
          (7u131-2.6.9-0ubuntu0.14.04.2)
    - OpenJDK 64-Bit Server VM (build 24.131-b00, mixed mode)
- For executing the *.R scripts:
    - R has to be installed; we used R version 3.4.0 (2017-04-21)
     - The following packages have to be installed as part of the R
          environment:
     - Hmisc; we used version 4.0.3
     - nortest; we used version 1.0.4



## [Com]mit [Ex]traction                           

The ComEx.sh script creates a "diff file" for each commit available in a git
repository (the set of commits to be extracted can be further restricted by a
commit list file; see "-l" option below). Such a "diff file" is named by the
SHA of the extracted commit and contains the committer date in the first line
and all changes introduced by the commit in the following lines. These changes
are obtained by calling the "git show" command with some additional options to
retrieve the entire content of each file changed by the respective commit. The
entire content is required to unambiguously differentiate changes to artifact-
specific and variability information during the commit analysis process.

```
Usage: bash ComEx.sh [-i DIR] [-o DIR]
    -i <git_dir>       specify the directory of the git repository
    -o <output_dir>    specify the directory to save the "diff files" to
    -l <commit_list>   specify a file containing the commits (SHA) to extract
                       [optional]. Each line of this file has to contain a
                       single commit SHA without leading or trailing
                       whitespaces.
```


## [Com]mit [An]alysis                             

The ComAn.jar Java-tool analyzes each "diff file" in the given input directory
(see "-i" option below). Similar to the commit extraction process, the set of
"diff files" (commits) can be further restricted (see "-l" option below). The
analysis investigates the changes introduced by each commit in order to count
the number of changed files belonging to a specific type of artifacts:
    - Variability model artifacts: Kconfig* files
    - Source code artifacts: *.c*, *.h*, *.S* files
    - Build artifacts: Makefile* and Kbuild* files

For each of these types of artifacts, the analysis counts the number of changed
lines containing artifact-specific information and the number of changed lines
containing variability information as follows:
    - Variability model artifacts: all non-empty changed lines are counted as
      variability information as long as they do not define help texts, which
      are counted as artifact-specific information
    - Source code artifacts: all non-empty changed lines are counted as
      artifact-specific information as long as they do not include references
      to configuration options (CONFIG_* symbols) or closing statements, which
      are related to opening statements including such references; in this case
      the lines are counted as variability information
    - Build artifacts: all non-empty changed lines are counted as artifact-
      specific information as long as they do not include references to
      configuration options (CONFIG_* symbols) or closing statements, which are
      related to opening statements including such references; in this case the
      lines are counted as variability information

The results of the analysis are the following three files:
    - ComAn_Summary.tsv: provides an overview on the changes introduced over
      all analyzed commits by summing up the detailed numbers elicited during
      the analysis
    - ComAn_Results.tsv: contains detailed information on the number of changes
      introduced by each analyzed commit in a separate line
    - ComAn_Unanalyzed.txt: contains the names of the "diff files", which were
      not analyzed, e.g. as the corresponding commits do not introduce changes
      to the contents of files but only change their permissions; if such
      commits are not available in the repository, this file may be missing.

Usage: java -jar ComAn.jar [-i DIR] [-o DIR]
    -i <input_dir>     specify the directory containing the "diff files"
                       (commit information) as extracted by the ComEx.sh script
    -o <output_dir>    specify the directory for saving the analysis results to
    -l <commit_list>   specify a file containing the commits (SHA) to analyze
                       [optional]. Each line of this file has to contain a
                       single commit SHA without leading or trailing
                       whitespaces.
    -d                 display debug information [optional]
    -h                 print this text
    -w                 display additional warnings [optional]



## [Com]mit [Vi]sualization                     

The ComVi*.R scripts visualize the analysis results of the ComAn.jar tool and
derive further statistics from the numbers of the ComAn_Results.tsv file (see
"Commit Analysis" above). This set of scripts is designed to be executed as a
whole by starting the ComVi.R script (see "Usage" below), which in turn calls
the other scripts that depend on some utility functions of the ComVi.R script.

```
Usage: Rscript ComVi.R <results_file> <output_dir>
    <results_file>     specify the absolute path to the ComAn_Results.tsv file
                       which provides to input data to be visualized by this
                       (set of) script(s)
    <output_dir>       specify the directory for saving the graphs and
                       statistics to
```



## Reproduction                              
The Reproduction.sh script calls the different tools and scripts of this
archive to provide exactly the same results as presented in our technical
report [1]. Further, this script also checks the availability of the required
software (see "Overview" above) to execute the tools and scripts. If some of
the requirements are not satisfied, the script informs about the missing
software and shows an example of how we installed that software. The
reproduction requires all tools and scripts of this archive to be located in
the same directory as this script.

```
Usage: bash Reproduction.sh [-t SPL] [-r DIR] [-o DIR]
    -t [Ll]inux || [Cc]oreboot  specify the target software product line for
                                which the analysis should be reproduced
    -r <repodir>                specify the directory to clone the target
                                repository to
    -o <outputdir>              specify the directory to save the output of the
                                entire analysis to
    -h                          show this message
```

[1] Christian Kröher and Klaus Schmid. A Commit-Bases Analysis of Software
    Product Line Evolution: Two Case Studies. Report No. 2/2017, SSE 2/17/E,
    2017.
