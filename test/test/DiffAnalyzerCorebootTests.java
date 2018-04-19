package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import diff.DiffAnalyzer;
import main.ComAnLogger;
import main.ComAnLogger.MessageType;

/**
 * This class provides some {@link DiffAnalyzer} tests based on real commits taken from the
 * coreboot-repository.
 * 
 * @author Christian Kr�her
 *
 */
public class DiffAnalyzerCorebootTests extends AbstractTests {

	/**
	 * The {@link File} defining the directory where the commits are stored.
	 */
	private static final File TEST_INPUT_DIR = new File("testdata/test_input_coreboot");
	
	/**
	 * The {@link File} defining the text file (stored in {@link #TEST_INPUT_DIR}),
	 * which contains tab-separated values. Each line of the file defines the expected values, like number
	 * of changed model files, changed model lines, etc., for a single commit in {@link #TEST_INPUT_DIR},
	 * which were manually counted.<br><br>
	 * 
	 * <b>See also</b> the readme.txt in the top-level directory of this project for further information on this file
	 */
	private static final File EXPECTED_VALUES_FILE = new File(TEST_INPUT_DIR, "coreboot_ExpectedValues.txt");
	
	/**
	 * The number of expected values for each commit in {@link #EXPECTED_VALUES_FILE}.<br><br>
	 * 
	 * This number if used to check whether all values for testing a specific commit are available. If not,
	 * the specific commit will not be part of the test-set.<br><br>
	 * 
	 * <b>See also</b> the readme.txt in the top-level directory of this project for further information on this value
	 */
	private static final int EXPECTED_VALUES_NUM = 9;
	
	/**
	 * The {@link List} of commit files loaded from {@link #TEST_INPUT_DIR}.
	 */
	private static List<File> testCommits;
	
	/**
	 * The {@link HashMap} containing the expected analysis result values for each commit in {@link #TEST_INPUT_DIR}
	 * loaded from {@link #EXPECTED_VALUES_FILE}.<br><br>
	 * 
	 * For each entry in this map the <code>key</code> is a string containing the commit number and the
	 * <code>value</code> is a {@link Vector} of integers representing the set of expected analysis result values
	 * for this commit. 
	 */
	private static HashMap<String, Vector<Integer>> commitExpectedValuesMap;
	
	/**
	 * Load all commits in {@link #TEST_INPUT_DIR} into {@link #testCommits} and trigger loading the expected
	 * values by calling {@link #loadExpectedValues()}.
	 */
	@BeforeClass
	public static void loadTestCommits() {
		File[] testInputDirFiles = TEST_INPUT_DIR.listFiles();
		testCommits = new ArrayList<File>();
		for (int i = 0; i < testInputDirFiles.length; i++) {
			String testInputFileName = testInputDirFiles[i].getName();
			if (testInputFileName.endsWith(".txt") && !testInputDirFiles[i].getName().equals(EXPECTED_VALUES_FILE.getName())) {
				testCommits.add(testInputDirFiles[i]);
			}
		}
		loadExpectedValues();
	}
	
	/**
	 * Load the expected analysis result values from {@link #EXPECTED_VALUES_FILE} into {@link #commitExpectedValuesMap}.
	 */
	public static void loadExpectedValues() {
		List<String> expectedValuesLines = TestUtils.readFile(EXPECTED_VALUES_FILE);
		List<File> testCommitBlacklist;
		if (expectedValuesLines != null && !expectedValuesLines.isEmpty()) {
			testCommitBlacklist = new ArrayList<File>();
			commitExpectedValuesMap = new HashMap<String, Vector<Integer>>();
			for (File testCommit : testCommits) {
				Vector<Integer> expectedValuesVector = null;
				String testCommitNumber = testCommit.getName().substring(0, testCommit.getName().length() - 4);
				int expectedValuesLinesCounter = 0;
				while (expectedValuesVector == null && expectedValuesLinesCounter < expectedValuesLines.size()) {
					String expectedValuesLine = expectedValuesLines.get(expectedValuesLinesCounter);
					if (expectedValuesLine.startsWith(testCommitNumber)) {
						expectedValuesVector = new Vector<Integer>();
						String[] expectedValues = expectedValuesLine.split("\t");
						// expectedValues[0] is the commit number; not needed here
						for (int i = 1; i < expectedValues.length; i++) {
							expectedValuesVector.add(Integer.parseInt(expectedValues[i]));
						}
					}
					expectedValuesLinesCounter++;
				}
				if (expectedValuesVector == null) {
					ComAnLogger.getInstance().log("DiffAnalyzerCorebootTests", "Loading expected values for \"" + testCommitNumber + "\" failed",
							"No expected values found for this commit - will be removed from test set", MessageType.ERROR);
					testCommitBlacklist.add(testCommit);
				} else if (expectedValuesVector.size() != EXPECTED_VALUES_NUM) {
					ComAnLogger.getInstance().log("DiffAnalyzerCorebootTests", "Loading expected values for \"" + testCommitNumber + "\" failed",
							"Number of expected values is \"" + expectedValuesVector.size() + "\" but expected \"" + EXPECTED_VALUES_NUM 
							+ "\" - will be removed from test set", MessageType.ERROR);
					testCommitBlacklist.add(testCommit);
				} else {
					commitExpectedValuesMap.put(testCommitNumber, expectedValuesVector);
				}
			}
			testCommits.removeAll(testCommitBlacklist);
		}
	}
	
	/**
	 * Test the {@link DiffAnalyzer} by comparing the analysis results against the expected values defined
	 * in {@link #EXPECTED_VALUES_FILE} for each commit of the coreboot-repository.
	 */
	@Test
	public void testCommits() {
		for (File testCommit : testCommits) {
			String testCommitNumber = testCommit.getName().substring(0, testCommit.getName().length() - 4);
			Vector<Integer> expectedValues = commitExpectedValuesMap.get(testCommitNumber);
			this.compareResultsToExpectedValues("DiffAnalyzerCorebootTests", TEST_INPUT_DIR, testCommit.getName(),
					expectedValues.get(0).intValue(), expectedValues.get(3).intValue(), expectedValues.get(4).intValue(),
					expectedValues.get(1).intValue(), expectedValues.get(5).intValue(), expectedValues.get(6).intValue(),
					expectedValues.get(2).intValue(), expectedValues.get(7).intValue(), expectedValues.get(8).intValue());
		}
	}
}
