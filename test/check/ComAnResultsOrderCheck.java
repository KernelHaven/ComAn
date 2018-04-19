package check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.ComAnLogger;
import main.ComAnLogger.MessageType;

public class ComAnResultsOrderCheck {
	
	/**
	 * The name (id) of this class for logging information.
	 */
	private static final String CLASS_ID = ComAnResultsOrderCheck.class.getSimpleName();

	/**
	 * The directory in which the ComAn result files are located. 
	 */
	private static final File COMAN_RESULTS_DIR = new File("testdata/check_input/");
	
	private static String previousCommitDate = null;
	
	public static void main(String[] args) {
		if (COMAN_RESULTS_DIR.exists() && COMAN_RESULTS_DIR.isDirectory()) {
			File[] resultFiles = COMAN_RESULTS_DIR.listFiles();
			if (resultFiles != null && resultFiles.length > 0) {
				for (int i = 0; i < resultFiles.length; i++) {
					File resultFile = resultFiles[i];
					String resultFileName = resultFile.getName();
					System.out.println("Checking " + resultFileName + "----");
					List<String> resultFileContent = readFile(resultFile);
					if (!resultFileContent.isEmpty()) {
						// Ignore first line containing column titles
						for (int j = 1; j < resultFileContent.size(); j++) {
							String fileContentLine = resultFileContent.get(j);
							String[] splittedFileContentLine = fileContentLine.split("\\s+");
							if (splittedFileContentLine.length > 0) {
								String commitDate = splittedFileContentLine[0];
								if (!checkCommitOrder(commitDate)) {
									System.out.println("Commits not in order:");
									System.out.println("\tCurrent commit date: " + commitDate);
									System.out.println("\tPrevious commit date: " + previousCommitDate);
								}
								previousCommitDate = commitDate;
							}
						}
					} else {
						System.err.println(resultFileName + " is empty");
					}
					System.out.println("Checking " + resultFileName + " done ----\n");
				}
			} else {
				System.err.println("ComAn results directory is empty: " + resultFiles.length + " file(s) available");
			}
		} else {
			System.err.println("ComAn results directory does not exist or is not a directory: " + COMAN_RESULTS_DIR.getAbsolutePath());
		}
	}
	
	private static boolean checkCommitOrder(String commitDate) {
		boolean commitsInOrder = false;
		if (!commitDate.isEmpty()) {
			if (previousCommitDate != null && !previousCommitDate.isEmpty()) {
				if (getYear(commitDate) < getYear(previousCommitDate)) {
					commitsInOrder = true;
				} else if (getYear(commitDate) == getYear(previousCommitDate)) {
					if (getMonth(commitDate) < getMonth(previousCommitDate)) {
						commitsInOrder = true;
					} else if (getMonth(commitDate) == getMonth(previousCommitDate)
							&& getDay(commitDate) <= getDay(previousCommitDate)) {
						commitsInOrder = true;
					}
				}
			}
		}
		return commitsInOrder;
	}
	
	private static int getYear(String commitDate) {
//		System.out.println("Year: " + Integer.parseInt(commitDate.substring(0, 4)));
		return Integer.parseInt(commitDate.substring(0, 4));
	}
	
	private static int getMonth(String commitDate) {
//		System.out.println("Month: " + Integer.parseInt(commitDate.substring(5, 7)));
		return Integer.parseInt(commitDate.substring(5, 7));
	}
	
	private static int getDay(String commitDate) {
//		System.out.println("Day: " + Integer.parseInt(commitDate.substring(8, 10)));
		return Integer.parseInt(commitDate.substring(8, 10));
	}
	
	/**
	 * Read the content of the given file and return a list of strings in which each string
	 * contains a single line of the content of the file.
	 * 
	 * @param file the {@link File} the content should be read from
	 * @return a {@link List} of {@link String}s representing the line-wise content of the
	 * given file; may return <code>null</code> if the given file cannot be read	
	 */
	private static List<String> readFile(File file) {
		List<String> fileLines = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;		
		try {
			fileLines = new ArrayList<String>();
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String fileLine;
			while ((fileLine = bufferedReader.readLine()) != null) {
				fileLines.add(fileLine);
			}
		} catch (IOException e) {
			ComAnLogger.getInstance().log(CLASS_ID, "Reading file \"" + file.getAbsolutePath() + "\" failed", 
					e.getMessage(), MessageType.ERROR);
		} finally {
			// Close the readers in any case
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					ComAnLogger.getInstance().log(CLASS_ID, "Closing file reader for \"" 
							+ file.getAbsolutePath() + "\" failed", e.getMessage(), MessageType.ERROR);
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					ComAnLogger.getInstance().log(CLASS_ID, "Closing buffered reader for \""
							+ file.getAbsolutePath() + "\" failed", e.getMessage(), MessageType.ERROR);
				}
			}
		}
		return fileLines;
	}
}
