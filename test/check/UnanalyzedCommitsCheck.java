package check;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.ComAnLogger;
import main.ComAnLogger.MessageType;

public class UnanalyzedCommitsCheck {
	
	/**
	 * The name (id) of this class for logging information.
	 */
	private static final String CLASS_ID = UnanalyzedCommitsCheck.class.getSimpleName();
	
	private static File commitsDir;
	private static File unanalyzedCommitsFile;
	private static File outputDir;
	private static String splName;
	
	public static void main(String[] args) {
		/*
		 * Input:
		 * 	directory in which the extracted commits are located (as provided by ComEx-script)
		 * 	file containing the unanalyzed commits (as provided by ComAn-tool)
		 * 	directory in which the output file of this check will be saved
		 * Output:
		 * 	file containing the content of all unanalyzed commits 
		 */
		
		/*
		 * G:\Repository-Evolution-Analysis\linux-results
		 * G:\Repository-Evolution-Analysis\linux-results\ComAn_Unanalyzed.txt
		 * C:\Users\kroeher\Desktop\
		 * Linux
		 */
		setupCheck();
		if (commitsDir != null && unanalyzedCommitsFile != null && outputDir != null && splName != null && !splName.trim().isEmpty()) {
			summarizeUnanalyzedCommitsContents();
		} else {
			System.err.println("Too less input!");
		}
	}
	
	private static void summarizeUnanalyzedCommitsContents() {
		List<String> unanalyzedCommitsList = readFile(unanalyzedCommitsFile);
		if (unanalyzedCommitsList != null) {
			File[] commits = commitsDir.listFiles();
			if (commits != null && commits.length > 0) {
				File summaryFile = new File(outputDir + "\\" + splName + "_unanalyzed-summary.txt");
				if (summaryFile.exists()) {
					System.out.println("Deleting old summary file");
					summaryFile.delete();
				}
				System.out.println("Creating summary file " + summaryFile.getAbsolutePath());
				try {
					if (!summaryFile.createNewFile()) {							
						System.err.println("Creating summary file failed");
						System.err.println("\t" + summaryFile);
					} else {
						for (String unanalyzedCommit : unanalyzedCommitsList) {
							boolean unanalyzedCommitFound = false;
							int commitsCounter = 0;
							while (!unanalyzedCommitFound && commitsCounter < commits.length) {
								if (commits[commitsCounter].getName().equals(unanalyzedCommit)) {
									System.out.println("Found");
									List<String> unanalyzedCommitContent = readFile(commits[commitsCounter]);
									unanalyzedCommitContent.add(0, "### Start: " + unanalyzedCommit + " ###");
									unanalyzedCommitContent.add("###  End: " + unanalyzedCommit + " ###");
									writeFile(unanalyzedCommitContent, summaryFile);
								}
								commitsCounter++;
							}
						}
						System.out.println("Creating summary file done");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void setupCheck() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		// Ask for directory containing the extracted commits (as provided by ComEx-script)
		try {
			System.out.print("Enter absolute path to commit directory (as provided by ComEx-script): ");
			commitsDir = getFilesystemObject(reader.readLine(), true);
			System.out.print("Enter absolute path to unanalyzed commits file (as provided by ComAn-tool): ");
			unanalyzedCommitsFile = getFilesystemObject(reader.readLine(), false);
			System.out.print("Enter absolute path to output directory: ");
			outputDir = getFilesystemObject(reader.readLine(), true);
			System.out.print("Enter name of the target product line: ");
			splName = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static File getFilesystemObject(String path, boolean isDirectory) {
		File filesystemObject = null;
		if (path != null && !path.trim().isEmpty()) {
			filesystemObject = new File(path);
			if (!filesystemObject.exists()
					|| (isDirectory && !filesystemObject.isDirectory())
					|| (!isDirectory && filesystemObject.isDirectory())) {
				filesystemObject = null;
			}
		}
		return filesystemObject;
	}
	
	private static void writeFile(List<String> content, File file) {
		StringBuilder fileContentBuilder = new StringBuilder();
		
		for (String contentLine : content) {
			fileContentBuilder.append(contentLine);
			fileContentBuilder.append("\n");
		}
		fileContentBuilder.append("\n");
		try {
			FileWriter resultFileWriter = new FileWriter(file, true);
			BufferedWriter bufferedResultWriter = new BufferedWriter(resultFileWriter);
			bufferedResultWriter.write(fileContentBuilder.toString());
			bufferedResultWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
