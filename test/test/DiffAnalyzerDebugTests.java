package test;

import java.io.File;

import org.junit.Test;

import diff.DiffAnalyzer;

/**
 * This class provides some {@link DiffAnalyzer} tests based on real input files from open
 * source repositories.
 * 
 * These tests were used to debug problems with correct counting of changed lines.
 * 
 * @author Christian Kr�her
 *
 */
public class DiffAnalyzerDebugTests extends AbstractTests {

	/**
	 * The directory in which the input files are located. 
	 */
	private static final File DEBUG_TEST_INPUT_DIR = new File("testdata/test_debug_input");
	
	/**
	 * Test the {@link DiffAnalyzer} by providing selected, real commits previously causing problems. This
	 * method may be extended for debugging further problems in future.
	 */
	@Test
	public void testSelectedCommitsForDebugging() {
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "882cbcd.txt", 0, 0, 0, 0, 0, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "4694836.txt", 0, 0, 0, 1, 13, 0, 1, 2, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "c8eedd5.txt", 0, 0, 0, 2, 3, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "e73fda8.txt", 0, 0, 0, 2, 298, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "5332369.txt", 0, 0, 0, 2, 0, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "caffb6e.txt", 0, 0, 0, 5, 622, 0, 1, 6, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "62a40a0.txt", 0, 0, 0, 0, 0, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "4da1aa8.txt", 0, 0, 0, 11, 270, 13, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "11302f3.txt", 1, 0, 10, 1, 9, 2, 1, 0, 1);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "9344bde.txt", 0, 0, 0, 11, 48, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "5c22825.txt", 0, 0, 0, 8, 1600, 18, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "8f372d0.txt", 0, 0, 0, 4, 342, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "5b35300.txt", 0, 0, 0, 4, 11, 4, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "503e4fe.txt", 1, 0, 1, 1, 7, 6, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "892d129.txt", 3, 0, 81, 15, 1918, 44, 1, 19, 5);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "911cedf.txt", 1, 3, 3, 4, 69, 8, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "ce011ec.txt", 0, 0, 0, 7, 82, 5, 1, 6, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "e15dfc1.txt", 0, 0, 0, 1, 372, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "9c8a06a.txt", 0, 0, 0, 4, 498, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "540ae01.txt", 0, 0, 0, 7, 207, 0, 0, 0, 0);
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "05f26fc.txt", 0, 0, 0, 12, 1185, 0, 1, 4, 0); // Old code lines 3301
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "2c018fb.txt", 0, 0, 0, 29, 949, 12, 0, 0, 0); // Old code lines 1414
		this.compareResultsToExpectedValues("DiffAnalyzerDebugTests", DEBUG_TEST_INPUT_DIR, "45cc550.txt", 0, 0, 0, 4, 649, 12, 0, 0, 0); // Old code lines 747
	}
}
