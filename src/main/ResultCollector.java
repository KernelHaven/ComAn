package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import diff.DiffAnalyzer;
import main.ComAnLogger.MessageType;

/**
 * This class is used to write the results of the analysis to files.<br><br>
 * 
 * There are two files that will be created:
 * <ul>
 * <li>ComAn_Results.tsv: the general data extracted from each commit</li>
 * <li>ComAn_Summary.tsv: the summary of the numbers, e.g. the sum and average of changed files, etc.</li>
 * </ul>
 * 
 * @author Christian Kroeher
 *
 */
public class ResultCollector {
	
	/**
	 * The name (id) of this class for logging information.
	 */
	private static final String CLASS_ID = ResultCollector.class.getSimpleName();
	
	/**
	 * Singleton instance of this class.
	 */
	private static ResultCollector instance = new ResultCollector();
	
	/**
	 * The number of analyzed commits.
	 */
	private long analyzedCommitsCounter;
	
	/**
	 * The number of analyzed commits changing artifact-specific information only.
	 */
	private long commitsChangingArtifactInfoCounter;
	
	/**
	 * The number of analyzed commits changing variability information only.
	 */
	private long commitsChangingVariabilityInfoCounter;
	
	/**
	 * The number of analyzed commits changing artifact-specific and variability information.
	 */
	private long commitsChangingArtifactAndVariabilityInfoCounter;
	
	/**
	 * The number of changed lines containing artifact-specific information only.
	 * This attribute is used to count the number of such lines over all commits,
	 * where each commit changes artifact-specific information only (no variability information).
	 */
	private long changedArtifactInfoLinesOnlyCounter;
	
	/**
	 * The number of changed lines containing variability information only.
	 * This attribute is used to count the number of such lines over all commits,
	 * where each commit changes variability information only (no artifact-specific information).
	 */
	private long changedVariabilityInfoLinesOnlyCounter;
	
	/**
	 * The number of changed lines containing artifact-specific information only.
	 * This attribute is used to count the number of such lines over all commits,
	 * where each commit changes both artifact-specific and variability information.
	 */
	private long changedArtifactInfoLinesCounter;
	
	/**
	 * The number of changed lines containing variability information only.
	 * This attribute is used to count the number of such lines over all commits,
	 * where each commit changes both artifact-specific and variability information.
	 */
	private long changedVariabilityInfoLinesCounter;
	
	/**
	 * The number of changed model lines (sum over all analyzed commits).
	 */
	private long changedModelLinesCounter;
	
	/**
	 * The number of changed model lines containing variability information
	 * (sum over all analyzed commits).
	 */
	private long changedModelVarLinesCounter;
	
	/**
	 * The number of changed source code lines (sum over all analyzed commits).
	 */
	private long changedSourceLinesCounter;
	
	/**
	 * The number of changed source code lines containing variability information
	 * (sum over all analyzed commits).
	 */
	private long changedSourceVarLinesCounter;
	
	/**
	 * The number of changed source code lines (sum over all analyzed commits).
	 */
	private long changedBuildLinesCounter;
	
	/**
	 * The number of changed build lines containing variability information
	 * (sum over all analyzed commits).
	 */
	private long changedBuildVarLinesCounter;
	
	/**
	 * The option to identify whether the result header (line with column titles
	 * for result file) is already written. The default value is <code>false</code>
	 * but will be changed to <code>true</code> the first time {@link #addResults(DiffAnalyzer, File)}
	 * is called. 
	 */
	private boolean resultHeaderWritten;

	/**
	 * Construct a new {@link ResultCollector}.
	 */
	private ResultCollector() {
		analyzedCommitsCounter = 0;
		commitsChangingArtifactInfoCounter = 0;
		commitsChangingVariabilityInfoCounter = 0;
		changedArtifactInfoLinesCounter = 0;
		changedArtifactInfoLinesOnlyCounter = 0;
		changedVariabilityInfoLinesCounter = 0;
		changedVariabilityInfoLinesOnlyCounter = 0;
		changedModelLinesCounter = 0;
		changedModelVarLinesCounter = 0;
		changedSourceLinesCounter = 0;
		changedSourceVarLinesCounter = 0;
		changedBuildLinesCounter = 0;
		changedBuildVarLinesCounter = 0;
		resultHeaderWritten = false;
	}
	
	/**
	 * Return the single instance of the {@link ResultCollector}.
	 * 
	 * @return the single instance of the {@link ResultCollector}
	 */
	public static ResultCollector getInstance() {
		return instance;
	}
	
	/**
	 * Add the numbers provided by the given {@link DiffAnalyzer} to the end of the given
	 * result file as a new line and add these numbers to the respective sums over all commits.
	 * 
	 * @param diffAnalyzer the {@link DiffAnalyzer} which provides the numbers to be added
	 * @param resultFile a {@link File} to which the provided numbers will be added in terms
	 * of a appended new line
	 */
	public void addResults(DiffAnalyzer diffAnalyzer, File resultFile) {
		String analyzedCommit = diffAnalyzer.getCommitNumber();
		if (analyzedCommit != null && !analyzedCommit.isEmpty()) {
			try {
				StringBuilder resultLineBuilder = new StringBuilder();
				// The final value is used in writeSummary(File, int)
				analyzedCommitsCounter++;
				// Get new analysis results
				int newChangedModelFilesCount = diffAnalyzer.getChangedModelFilesCount();
				int newChangedModelLinesCount = diffAnalyzer.getChangedModelLinesCount();
				int newChangedModelVarLinesCount = diffAnalyzer.getChangedModelVarLinesCount();
				int newChangedSourceFilesCount = diffAnalyzer.getChangedSourceFilesCount();
				int newChangedSourceLinesCount = diffAnalyzer.getChangedSourceLinesCount();
				int newChangedSourceVarLinesCount = diffAnalyzer.getChangedSourceVarLinesCount();
				int newChangedBuildFilesCount = diffAnalyzer.getChangedBuildFilesCount();
				int newChangedBuildLinesCount = diffAnalyzer.getChangedBuildLinesCount();
				int newChangedBuildVarLinesCount = diffAnalyzer.getChangedBuildVarLinesCount();
				// Add new results to overall counters (over all commits)
				changedModelLinesCounter += newChangedModelLinesCount;
				changedModelVarLinesCounter += newChangedModelVarLinesCount;
				changedSourceLinesCounter += newChangedSourceLinesCount;
				changedSourceVarLinesCounter += newChangedSourceVarLinesCount;
				changedBuildLinesCounter += newChangedBuildLinesCount;
				changedBuildVarLinesCounter += newChangedBuildVarLinesCount;
				// Increase commit and change counters depending on type of lines being changed
				int newChangedArtifactInfoLinesSum = newChangedModelLinesCount + newChangedSourceLinesCount + newChangedBuildLinesCount;
				int newChangedVariabilityInfoLinesSum = newChangedModelVarLinesCount + newChangedSourceVarLinesCount + newChangedBuildVarLinesCount;
				if (newChangedArtifactInfoLinesSum == 0 && newChangedVariabilityInfoLinesSum > 0) {
					// Only lines including variability information changed
					commitsChangingVariabilityInfoCounter++;
					changedVariabilityInfoLinesOnlyCounter += newChangedVariabilityInfoLinesSum;
				} else if (newChangedArtifactInfoLinesSum > 0 && newChangedVariabilityInfoLinesSum == 0) {
					// Only lines including artifact-specific information changed
					commitsChangingArtifactInfoCounter++;
					changedArtifactInfoLinesOnlyCounter += newChangedArtifactInfoLinesSum;
				} else if (newChangedArtifactInfoLinesSum > 0 && newChangedVariabilityInfoLinesSum > 0) {
					// Lines including variability and artifact-specific information changed
					commitsChangingArtifactAndVariabilityInfoCounter++;
					changedArtifactInfoLinesCounter += newChangedArtifactInfoLinesSum;
					changedVariabilityInfoLinesCounter += newChangedVariabilityInfoLinesSum;
				}
				/*
				 * Write the header of the result file (column titles) once
				 * this method is called the first time.
				 */
				if (!resultHeaderWritten) {
					resultLineBuilder.append("Date\tCommit\tCCF\tCCLAI\tCCLVI\t"
							+ "CBF\tCBLAI\tCBLVI\t"
							+ "CMF\tCMLAI\tCMLVI\n");
					resultHeaderWritten = true;
				}
				// The date of the analyzed commit
				resultLineBuilder.append(diffAnalyzer.getCommitDate() + "\t");
				// The commit number of the analyzed commit
				resultLineBuilder.append(analyzedCommit + "\t");
				/*
				 * Number of source code files, all lines, and lines containing variability
				 * information changed by analyzed commit.
				 */
				resultLineBuilder.append(newChangedSourceFilesCount + "\t");
				resultLineBuilder.append(newChangedSourceLinesCount + "\t");
				resultLineBuilder.append(newChangedSourceVarLinesCount + "\t");
				/*
				 * Number of build files, all lines, and lines containing variability
				 * information changed by analyzed commit.
				 */
				resultLineBuilder.append(newChangedBuildFilesCount + "\t");
				resultLineBuilder.append(newChangedBuildLinesCount + "\t");
				resultLineBuilder.append(newChangedBuildVarLinesCount + "\t");
				/*
				 * Number of variability model files, all lines, and lines containing variability
				 * information changed by analyzed commit.
				 */
				resultLineBuilder.append(newChangedModelFilesCount + "\t");
				resultLineBuilder.append(newChangedModelLinesCount + "\t");
				resultLineBuilder.append(newChangedModelVarLinesCount + "\n");
				// Append current results to result file
				FileWriter resultFileWriter = new FileWriter(resultFile, true);
				BufferedWriter bufferedResultWriter = new BufferedWriter(resultFileWriter);
				bufferedResultWriter.write(resultLineBuilder.toString());
				bufferedResultWriter.close();
			} catch (IOException e) {
				ComAnLogger.getInstance().log(CLASS_ID, "Saving results to \"" + resultFile.getAbsolutePath() + "\" failed",
						e.getMessage(), MessageType.ERROR);
			}
		}
	}
	
	/**
	 * Add a commit file to the list of unanalyzed commits in the given error file.
	 * 
	 * @param commitFileName the name of the commit file that was not analyzed due to errors
	 * @param unanalyzedFile the file containing all unanalyzed commits by file name 
	 */
	public void addUnanalyzed(String commitFileName, File unanalyzedFile) {
		try {
			StringBuilder errorLineBuilder = new StringBuilder();
			errorLineBuilder.append(commitFileName);
			errorLineBuilder.append("\n");
			
			FileWriter errorFileWriter = new FileWriter(unanalyzedFile, true);
			BufferedWriter bufferedErrorWriter = new BufferedWriter(errorFileWriter);
			bufferedErrorWriter.write(errorLineBuilder.toString());
			bufferedErrorWriter.close();
		} catch (IOException e) {
			ComAnLogger.getInstance().log(CLASS_ID, "Saving unanalyzed commit to \"" + unanalyzedFile.getAbsolutePath() + "\" failed",
					e.getMessage(), MessageType.ERROR);
		}
	}
	
	/**
	 * Write the (final) summary of the analysis, e.g. the sum of changed files over all commits, the
	 * average numbers of changed lines per commit, etc.
	 * 
	 * @param summaryFile the {@link File} to which the summary should be written; this file <b>must exist</b>
	 * and <b>must be accessible</b>
	 * @param commitFilesNum the number of commit files involved in the analysis (this number may vary from the
	 * number of actually analyzed commits due to missing diff information) 
	 */
	public void writeSummary(File summaryFile, int commitFilesNum) {
		try {
			StringBuilder summaryLineBuilder = new StringBuilder();
			// Column titles
			summaryLineBuilder.append("Counted Element\tNumber of Commits\tNumber of Changed Lines (artifact-specific)\tNumber of Changed Lines (variability)\n");
			// Commits available
			summaryLineBuilder.append("CAv\t" + commitFilesNum + "\n");
			// Commits analyzed
			summaryLineBuilder.append("CAn\t" + analyzedCommitsCounter + "\n");
			// Commits changing artifact-specific information only and corresponding sum of changed lines over all commits and file types
			summaryLineBuilder.append("CCAI\t" + commitsChangingArtifactInfoCounter + "\t" + changedArtifactInfoLinesOnlyCounter + "\n");
			// Commits changing variability information only and corresponding sum of changed lines over all commits and file types
			summaryLineBuilder.append("CCVI\t" + commitsChangingVariabilityInfoCounter + "\t\t" + changedVariabilityInfoLinesOnlyCounter + "\n");
			// Commits changing artifact-specific and variability information and corresponding sum of changed lines over all commits and file types
			summaryLineBuilder.append("CCAVI\t" + commitsChangingArtifactAndVariabilityInfoCounter + "\t" + changedArtifactInfoLinesCounter + "\t" + changedVariabilityInfoLinesCounter + "\n");
			// Changed model lines: artifact-specific, variability
			summaryLineBuilder.append("CML\t\t" + changedModelLinesCounter + "\t" + changedModelVarLinesCounter + "\n");
			// Changed source code lines: artifact-specific, variability
			summaryLineBuilder.append("CCL\t\t" + changedSourceLinesCounter + "\t" + changedSourceVarLinesCounter + "\n");
			// Changed build lines: artifact-specific, variability
			summaryLineBuilder.append("CBL\t\t" + changedBuildLinesCounter + "\t" + changedBuildVarLinesCounter  + "\n");
			// Append description of abbreviations to summary file
			summaryLineBuilder.append("\n\nDescription:\n");
			summaryLineBuilder.append("CAv\t[C]ommits [Av]ailable: number of all commits input to this analysis\n");
			summaryLineBuilder.append("CAn\t[C]ommits [An]alyzed: number of commits actually analyzed\n");
			summaryLineBuilder.append("\t    Some commits may not be analyzed due to no file changes\n");
			summaryLineBuilder.append("CCAI\t[C]ommits [C]hanging [A]rtifact-specific [I]nformation: number of commits that change at least one line of\n");
			summaryLineBuilder.append("\t    a) help text in a variability model file (no variability information)\n");
			summaryLineBuilder.append("\t    b) general source code in a source code file (no variability information)\n");
			summaryLineBuilder.append("\t    c) the general build process definition in a build file (no variability information)\n");
			summaryLineBuilder.append("CCVI\t[C]ommits [C]hanging [V]ariability [I]nformation: number of commits that change at least one line defining\n");
			summaryLineBuilder.append("\t    a) configuration options, etc. in a variability model file (variability information)\n");
			summaryLineBuilder.append("\t    b) references to configuration options in a source code file (variability information)\n");
			summaryLineBuilder.append("\t    c) references to configuration options in a build file (variability information)\n");
			summaryLineBuilder.append("CCAVI\t[C]ommits [C]hanging [A]rtifact-specific and [V]ariability [I]nformation: number of commits that change both types of information (see CCAI and CCVI)\n");
			summaryLineBuilder.append("CML\t[C]hanged [M]odel [L]ines: number of changed model lines over all analyzed commits\n");
			summaryLineBuilder.append("CCL\t[C]hanged source [C]ode [L]ines: number of changed source code lines over all analyzed commits\n");
			summaryLineBuilder.append("CBL\t[C]hanged [B]uild process[L]ines: number of changed build process lines over all analyzed commits\n");
			// Write the complete summary file
			FileWriter summaryFileWriter = new FileWriter(summaryFile, true);
			BufferedWriter bufferedSummaryWriter = new BufferedWriter(summaryFileWriter);
			bufferedSummaryWriter.write(summaryLineBuilder.toString());
			bufferedSummaryWriter.close();
		} catch (IOException e) {
			ComAnLogger.getInstance().log(CLASS_ID, "Saving summary to \"" + summaryFile.getAbsolutePath() + "\" failed",
					e.getMessage(), MessageType.ERROR);
		}
	}
}
