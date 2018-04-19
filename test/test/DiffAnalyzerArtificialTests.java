package test;

import java.io.File;

import org.junit.Test;

import diff.DiffAnalyzer;

/**
 * This class provides some {@link DiffAnalyzer} tests based on artificial input files.
 * 
 * These tests were used initially to check for correct counting of changed lines.
 * 
 * @author Christian Kr�her
 *
 */
public class DiffAnalyzerArtificialTests extends AbstractTests {
	
	/**
	 * The directory in which the artificial input files are located. 
	 */
	private static final File ARTIFICIAL_TEST_INPUT_DIR = new File("testdata/test_artificial_input");
	
	/**
	 * Test the {@link DiffAnalyzer} by providing some artificial changes to a model, source code,
	 * and build file.
	 */
	@Test
	public void testArtificialChangeDetection() {
		this.compareResultsToExpectedValues("DiffAnalyzerArtificialTests", ARTIFICIAL_TEST_INPUT_DIR, "modelChangeCommit.txt", 1, 45, 37, 0, 0, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerArtificialTests", ARTIFICIAL_TEST_INPUT_DIR, "sourceChangeCommit.txt", 0, 0, 0, 1, 14, 49, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerArtificialTests", ARTIFICIAL_TEST_INPUT_DIR, "buildChangeCommit.txt", 0, 0, 0, 0, 0, 0, 1, 6, 6);
	}
}