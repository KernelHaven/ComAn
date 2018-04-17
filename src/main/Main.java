package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import diff.DiffAnalyzer;
import main.ComAnLogger.MessageType;

public class Main {
	
	/**
	 * The name (id) of this class for logging information.
	 */
	private static final String CLASS_ID = Main.class.getSimpleName();
	
	/**
	 * The name of the result file containing a single line for each analyzed commit
	 * with the number of changed model files, source code files, build files, and the
	 * overall number of changed lines and changed lines containing variability information
	 * for each of these file types.
	 */
	private static final String RESULT_FILE_NAME = "ComAn_Results.tsv";
	
	/**
	 * The name of the summary file containing the overall numbers of all analyzed commits,
	 * e.g. the average number of changed model files, etc. with respect to the number of
	 * analyzed commits.
	 */
	private static final String SUMMARY_FILE_NAME = "ComAn_Summary.tsv";
	
	/**
	 * The name of the file containing the commits that were not analyzed,
	 * e.g. because they do not contain line-wise changes to files.
	 */
	private static final String UNANALYZED_FILE_NAME = "ComAn_Unanalyzed.txt";
	
	/**
	 * The command line options of this tool.
	 * See {@link #createOptions()}
	 */
	private static Options comanOptions;
	
	/**
	 * The directory where the commits can be found.
	 */
	private static File inputDir;
	
	/**
	 * The file which will contain the full result list of the analysis.
	 */
	private static File resultFile;
	
	/**
	 * The file which will contain the summary of the analysis, e.g. the
	 * sum and average of counted lines, etc. 
	 */
	private static File summaryFile;
	
	/**
	 * The file which will contain a list of unanalyzable commits, e.g.
	 * as they do not include line-wise changes. This file is only created
	 * if such commits are found.
	 */
	private static File unanalyzedFile;
	
	/**
	 * The file which contains a list of commit numbers. These numbers control
	 * the analysis in the way that only these commits will be analyzed. This
	 * file is optional and may be <code>null</code>.
	 */
	private static File commitListFile;

	/**
	 * Main entry point of this tool.
	 * 
	 * @param args the mandatory and optional parameters for configuring the tool
	 */
	public static void main(String[] args) {
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy hh:mm:ss");
		ComAnLogger.getInstance().log(CLASS_ID, "ComAn start: " + dateFormat.format(currentDate), null, MessageType.INFO);
		
		createOptions();
		if (parseOptions(args)) {
			analyzeCommits(inputDir, commitListFile, resultFile, summaryFile, unanalyzedFile);
		} else {
			printHelp();
		}
		
		currentDate = new Date();
		ComAnLogger.getInstance().log(CLASS_ID, "ComAn end: " + dateFormat.format(currentDate), null, MessageType.INFO);
	}
	
	/**
	 * Analyze individual commits using the {@link DiffAnalyzer} and write results to given
	 * result file.
	 * 
	 * @param inputDir a {@link File} specifying the directory containing the commit files
	 * @param commitListFile a {@link File} specifying a list of commits (SHA) for analysis; if
	 * this object is <code>null</code>, all commits in inputDir will be analyzed
	 * @param resultFile a {@link File} specifying the file for saving the commit-wise results
	 * of the analysis
	 * @param summaryFile a {@link File} specifying the file for saving the overall results of
	 * the analysis, e.g. average numbers, etc. as summary
	 * @param unanalyzedFile a {@link File} specifying the file for saving the commits that were
	 * not analyzed, e.g. because of missing line-wise changes
	 */
	private static void analyzeCommits(File inputDir, File commitListFile, File resultFile, File summaryFile, File unanalyzedFile) {
		ComAnLogger.getInstance().log(CLASS_ID, "Collecting commits", null, MessageType.INFO);
		String[] commitFiles = getCommitFile(commitListFile, inputDir);
		int commitFilesCount = commitFiles.length;
		ComAnLogger.getInstance().log(CLASS_ID, commitFilesCount + " commits found", null, MessageType.INFO);
		ComAnLogger.getInstance().log(CLASS_ID, "Analyzing commits", null, MessageType.INFO);
		File commitFile = null;
		DiffAnalyzer diffAnalyzer = null;
		for (int i = 0; i < commitFilesCount; i++) {
			ComAnLogger.getInstance().log(CLASS_ID, "Analyzing commit \"" + commitFiles[i] + "\"", null, MessageType.DEBUG);
			commitFile = new File(inputDir, commitFiles[i]);
			diffAnalyzer = new DiffAnalyzer(commitFile);
			if (!diffAnalyzer.getCommitNumber().isEmpty() && diffAnalyzer.analyze()) {				
				ResultCollector.getInstance().addResults(diffAnalyzer, resultFile);
			} else {
				ResultCollector.getInstance().addUnanalyzed(commitFiles[i], unanalyzedFile);
			}
		}
		ResultCollector.getInstance().writeSummary(summaryFile, commitFilesCount);
		ComAnLogger.getInstance().log(CLASS_ID, "Commits analyzed", null, MessageType.INFO);
	}
	
	/**
	 * Collect and return files by name from the given <code>inputDir</code>. If
	 * a <code>commitListFile</code> is given, only the files are returned that are
	 * part of that list. This list has to contain a single commit SHA per line.
	 * 
	 * @param commitListFile optional {@link File} containing the commit SHA for analysis;
	 *  may be <code>null</code>
	 * @param inputDir a {@link File} specifying the directory containing the commit files
	 * @return a string array containing all commit files by name of the given <code>inputDir</code>
	 * or - if specified - only those files defined in the <code>commitListFile</code>; may be
	 * empty if the given <code>inputDir</code> is empty
	 */
	private static String[] getCommitFile(File commitListFile, File inputDir) {
		String[] commitFileArray = inputDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean isAccepted = false;
				if (name.endsWith(".txt")) {
					isAccepted = true;
				}
				return isAccepted;
			}
		});;
		if (commitListFile != null) {
			/*
			 * Commit list file specified, thus, collect and return only those
			 * commit (files) in the input directory for analysis, that are part
			 * of that list. This could be all files but in the historical order
			 * of the commits
			 */
			List<String> tempCommitFileList = new ArrayList<String>();
			List<String> commitShaList = readFile(commitListFile);
			String commitFile = "";
			for (String commitSha : commitShaList) {
				for (int i = 0; i < commitFileArray.length; i++) {
					commitFile = commitFileArray[i];
					if (commitFile.equals(commitSha + ".txt")) {
						tempCommitFileList.add(commitFile);
					}
				}
			}
			commitFileArray = tempCommitFileList.toArray(new String[tempCommitFileList.size()]);
		}
		return commitFileArray;
	}
	
	/**
	 * Create a {@link File} object based on given argument.
	 * 
	 * @param arg a string representing a path to a directory
	 * @param isDir <code>true</code> if the given arg should represent an existing directory, <code>false</code> if it
	 * should be an existing file
	 * @return a file object if the given string contains a path to an <b>existing</b> directory; <code>null</null> otherwise
	 */
	private static File argToFile(String arg, boolean isDir) {
		File fileOrDir = new File (arg);
		if (!fileOrDir.exists()) {			
			if (isDir) {			
				if (!fileOrDir.isDirectory()) {
					fileOrDir = null;
				}
			} else {
				if (!fileOrDir.isFile()) {
					fileOrDir = null;
				}
			}
		}
		return fileOrDir;
	}
	
	/**
	 * Create the {@link Options} of this tool.
	 */
	private static void createOptions() {
		Option inputDirOpt = Option.builder("i")
				.required(true)
				.hasArg()
				.argName("input_dir")
				.desc("specify the directory containing the \"diff files\" (commit information) as extracted by the ComEx.sh script")
				.build();		
		Option outputDirOpt = Option.builder("o")
				.required(true)
				.hasArg()
				.argName("output_dir")
				.desc("specify the directory for saving the analysis results to")
				.build();
		Option commitListOpt = Option.builder("l")
				.required(false)
				.hasArg()
				.argName("commit_list")
				.desc("specify a file containing the commits (SHA) to analyze [optional]. Each line of this file has to contain a"
				        + "single commit SHA without leading or trailing whitespaces")
				.build();
		Option helpOption = new Option("h", "print this message");
		Option enableWarningsOpt = Option.builder("w")
				.required(false)
				.desc("display additional warnings [optional]")
				.build();
		Option enableDebugOpt = Option.builder("d")
				.required(false)
				.desc("display debug information [optional]")
				.build();
		
		comanOptions = new Options();
		comanOptions.addOption(helpOption);
		comanOptions.addOption(inputDirOpt);
		comanOptions.addOption(outputDirOpt);
		comanOptions.addOption(commitListOpt);
		comanOptions.addOption(enableWarningsOpt);
		comanOptions.addOption(enableDebugOpt);
	}
	
	/**
	 * Parse the given arguments as options to this tool and configure the tool.
	 * 
	 * @param args string array containing the arguments passed to this tool
	 * @return <code>true</code> if the mandatory arguments are provided and the
	 * tool is configured correctly; <code>false</code> otherwise. 
	 */
	private static boolean parseOptions(String[] args) {
		boolean configuredCorrectly = true; 
		CommandLineParser commandLineParser = new DefaultParser();
		try {
			CommandLine commandLine = commandLineParser.parse(comanOptions, args);			
			/*
			 * As -i and -o options are defined mandatory (required) above, an
			 * exception will be thrown if one or both options are missing.
			 * Thus, there is no need to have further checks on their presence
			 * here. 
			 */
			inputDir = argToFile(commandLine.getOptionValue("i"), true);
			if (inputDir != null) {
				File outputDir = argToFile(commandLine.getOptionValue("o"), true);
				if (outputDir != null) {
					String commitListOptionValue = commandLine.getOptionValue("l");
					if (commitListOptionValue != null && !commitListOptionValue.isEmpty()) {
						commitListFile = argToFile(commitListOptionValue, false);
						if (commitListFile == null) {
							ComAnLogger.getInstance().log(CLASS_ID, "Creating commit list file object failed", 
									"\"" + commitListOptionValue + "\" is not a file or does not exist", MessageType.ERROR);
							configuredCorrectly = false;
						}
					}
					if (configuredCorrectly) {
						if (commandLine.hasOption("w")) {
							ComAnLogger.getInstance().enableWarnings();
						}
						if (commandLine.hasOption("d")) {
							ComAnLogger.getInstance().enableDebug();
						}
						resultFile = new File(outputDir, RESULT_FILE_NAME);
						summaryFile = new File (outputDir, SUMMARY_FILE_NAME);
						unanalyzedFile = new File(outputDir, UNANALYZED_FILE_NAME);
						if (resultFile.exists()) {
							resultFile.delete();
						}
						if (summaryFile.exists()) {
							summaryFile.delete();
						}
						if (unanalyzedFile.exists()) {
							unanalyzedFile.delete();
						}
						try {
							resultFile.createNewFile();
							summaryFile.createNewFile();
							// Do not create unanalyzed commits file here. Create only if unanalyzable commits occur.
						} catch (IOException e) {
							ComAnLogger.getInstance().log(CLASS_ID, "Creating new output files failed", 
									e.getMessage(), MessageType.ERROR);
							configuredCorrectly = false;
						}
					}
				} else {
					ComAnLogger.getInstance().log(CLASS_ID, "Creating output directory object failed",
							"\"" + commandLine.getOptionValue("o") 
							+ "\" is not a directory or does not exist", MessageType.ERROR);
					configuredCorrectly = false;						
				}
			} else {				
				ComAnLogger.getInstance().log(CLASS_ID, "Creating input directory object failed",
						"\"" + commandLine.getOptionValue("i") 
						+ "\" is not a directory or does not exist", MessageType.ERROR);
				configuredCorrectly = false;	
			}
		} catch (ParseException e) {
			ComAnLogger.getInstance().log(CLASS_ID, "Parsing command line options failed", 
					e.getMessage(), MessageType.ERROR);
			configuredCorrectly = false;
		}
		return configuredCorrectly;
	}
	
	/**
	 * Print the help message to console.
	 */
	private static void printHelp() {
		System.out.println("\n\t[Com]mit [An]alyzer\n");
		System.out.println("The ComAn.jar Java-tool analyzes each \"diff file\" in the given input directory\n"
		        + "(see \"-i\" option below). Similar to the commit extraction process, the set of\n"
		        + "\"diff files\" (commits) can be further restricted (see \"-l\" option below). The\n"
		        + "analysis investigates the changes introduced by each commit in order to count\n"
		        + "the number of changed files belonging to a specific type of artifacts:\n"
		        + "    - Variability model artifacts: Kconfig* files\n"
		        + "    - Source code artifacts: *.c*, *.h*, *.S* files\n"
		        + "    - Build artifacts: Makefile* and Kbuild* files\n");
		System.out.println("For each of these types of artifacts, the analysis counts the number of changed\n"
		        + "lines containing artifact-specific information and the number of changed lines\n"
		        + "containing variability information as follows:\n"
		        + "    - Variability model artifacts: all non-empty changed lines are counted as\n"
		        + "      variability information as long as they do not define help texts, which\n"
		        + "      are counted as artifact-specific information\n"
		        + "    - Source code artifacts: all non-empty changed lines are counted as\n"
		        + "      artifact-specific information as long as they do not include references\n"
		        + "      to configuration options (CONFIG_* symbols) or closing statements, which\n"
		        + "      are related to opening statements including such references; in this case\n"
		        + "      the lines are counted as variability information\n"
		        + "    - Build artifacts: all non-empty changed lines are counted as artifact-\n"
		        + "      specific information as long as they do not include references to\n"
		        + "      configuration options (CONFIG_* symbols) or closing statements, which are\n"
		        + "      related to opening statements including such references; in this case the\n"
		        + "      lines are counted as variability information\n");
		System.out.println("The results of the analysis are the following three files:\n"
		        + "    - ComAn_Summary.tsv: provides an overview on the changes introduced over\n"
		        + "      all analyzed commits by summing up the detailed numbers elicited during\n"
		        + "      the analysis\n"
		        + "    - ComAn_Results.tsv: contains detailed information on the number of changes\n"
		        + "      introduced by each analyzed commit in a separate line\n"
		        + "    - ComAn_Unanalyzed.txt: contains the names of the \"diff files\", which were\n"
		        + "      not analyzed, e.g. as the corresponding commits do not introduce changes\n"
		        + "      to the contents of files but only change their permissions; if such\n"
		        + "      commits are not available in the repository, this file may be missing.\n");
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java -jar ComAn.jar [-i DIR] [-o DIR]", comanOptions);
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
