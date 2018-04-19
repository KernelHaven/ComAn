Adding a new commit for testing requires the following steps:

1. Place the text file containing the diff information in one of the "test_"-directories depending on the repository the commit belongs to.
    IMPORTANT: the name of that file has to be the commit-number and the extension has to be "txt"!
2. Enter a new row to the Excel-sheet in the directory the commit was placed in step 1 (commit number and manually counted lines).
3. Save the Excel-sheet
4. Save the Excel-sheet as tab delimited text file (same name, but with file extension "txt")
    IMPORTANT: do not rename the Excel-sheet nor the tab delimited text file as the name is used in the test case.

Thats it!

The tests are designed to read the tab delimited text file (Excel-sheet data) before executing the tests in order to load the expected values for the
different numbers (line counts). Missing numbers or false entries lead to ignoring the commit during test.

Correctly provided numbers for a specific commit are used to test the calculated numbers of the tool against them by commit-number matching (the 
file name of the commit against the first value of a row in the tab delimited text file (Excel-sheet data).


IMPORTANT:  The current number of values for each commit is currently 9. This number is hard-coded in the test-cases to check whether all values for
            specific commit are available. Thus, if less or more values will be entered, the commit will not be part of the test-set.
            If, in future, the overall number of analysis result values will change, the hard-coded number of expected values has to be changed too. 