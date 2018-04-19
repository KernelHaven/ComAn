package test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;

import diff.DiffAnalyzer;
import main.ComAnLogger;

/**
 * This abstract class provides global test methods and configurations that should be inherited/available
 * for all specific test classes.
 * 
 * @author Christian Kr�her
 *
 */
public abstract class AbstractTests {

	/**
	 * The option for enabling the debug messages of the {@link ComAnLogger} during
	 * development.
	 * By default, these messages are not printed.
	 */
	private static final boolean ENABLE_DEBUG_MESSAGES = false;
	
	/**
	 * Configure the {@link ComAnLogger} to print debug messages based on the value
	 * of {@link #ENABLE_DEBUG_MESSAGES}.
	 */
	@BeforeClass
	public static void configureLogger() {
		if (ENABLE_DEBUG_MESSAGES) {
			ComAnLogger.getInstance().enableDebug();
		}
	}
	
	/**
	 * Compare the given, expected values of changed files and lines of a specific commit 
	 * to the values provided by the {@link DiffAnalyzer} after analysis.
	 * 
	 * @param origin the name of the test class calling this method
	 * @param testInputDir the directory where the commit file is located
	 * @param commitFileName the name of the commit file of the form "[CommitSHA].txt" to analyze
	 * @param expectedChangedModelFiles the expected number of changed model files
	 * @param expectedChangedModelLines the expected number of changed model lines
	 * @param expectedChangedModelVarLines the expected number of changed model lines including variability information
	 * @param expectedChangedSourceFiles the expected number of changed source files
	 * @param expectedChangedSourceLines the expected number of changed source lines
	 * @param expectedChangedSourceVarLines the expected number of changed source lines including variability information
	 * @param expectedChangedBuildFiles the expected number of changed build files
	 * @param expectedChangedBuildLines the expected number of changed build lines
	 * @param expectedChangedBuildVarLines the expected number of changed build lines including variability information
	 */
	protected void compareResultsToExpectedValues(String origin, File testInputDir, String commitFileName, int expectedChangedModelFiles, 
			int expectedChangedModelLines,	int expectedChangedModelVarLines,	int expectedChangedSourceFiles,
			int expectedChangedSourceLines,	int expectedChangedSourceVarLines,	int expectedChangedBuildFiles,
			int expectedChangedBuildLines,	int expectedChangedBuildVarLines) {
		File commitFile = new File (testInputDir, commitFileName);
		DiffAnalyzer diffAnalyzer = new DiffAnalyzer(commitFile);
		diffAnalyzer.analyze();
		
		String identifiedFileName = diffAnalyzer.getCommitNumber() + ".txt";
		assertEquals("[" + origin + "] Commit number \"" + diffAnalyzer.getCommitNumber() 
		+ "\" must match file name (without extension) \"" + commitFileName + "\"",
		commitFileName, identifiedFileName);
		
		assertEquals("[" + origin + "] Changed model files must be " + expectedChangedModelFiles + " for \"" + commitFile + "\"",
				expectedChangedModelFiles, diffAnalyzer.getChangedModelFilesCount());
		assertEquals("[" + origin + "] Changed model lines must be " + expectedChangedModelLines + " for \"" + commitFile + "\"",
				expectedChangedModelLines, diffAnalyzer.getChangedModelLinesCount());
		assertEquals("[" + origin + "] Changed model lines including variability must be " + expectedChangedModelVarLines + " for \"" + commitFile + "\"",
				expectedChangedModelVarLines, diffAnalyzer.getChangedModelVarLinesCount());
		
		assertEquals("[" + origin + "] Changed code files must be " + expectedChangedSourceFiles + " for \"" + commitFile + "\"",
				expectedChangedSourceFiles, diffAnalyzer.getChangedSourceFilesCount());
		assertEquals("[" + origin + "] Changed code lines must be " + expectedChangedSourceLines + " for \"" + commitFile + "\"",
				expectedChangedSourceLines, diffAnalyzer.getChangedSourceLinesCount());
		assertEquals("Changed code lines including variability must be " + expectedChangedSourceVarLines + " for \"" + commitFile + "\"",
				expectedChangedSourceVarLines, diffAnalyzer.getChangedSourceVarLinesCount());
		
		assertEquals("[" + origin + "] Changed build files must be " + expectedChangedBuildFiles + " for \"" + commitFile + "\"",
				expectedChangedBuildFiles, diffAnalyzer.getChangedBuildFilesCount());
		assertEquals("[" + origin + "] Changed build lines must be " + expectedChangedBuildLines + " for \"" + commitFile + "\"",
				expectedChangedBuildLines, diffAnalyzer.getChangedBuildLinesCount());
		assertEquals("[" + origin + "] Changed build lines including variability must be " + expectedChangedBuildVarLines + " for \"" + commitFile + "\"",
				expectedChangedBuildVarLines, diffAnalyzer.getChangedBuildVarLinesCount());
	}
}
