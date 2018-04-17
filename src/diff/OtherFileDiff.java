package diff;

import main.ComAnLogger;
import main.ComAnLogger.MessageType;

/**
 * This class is used to count changes to other files than model, source code, or build files. As these files
 * are not specified in detail, there is no explicit count of variability changes. Currently, objects of this
 * class and the corresponding results are not part of the overall analysis results.
 * 
 * @author Christian Kroeher
 *
 */
public class OtherFileDiff extends FileDiff {
	
	/**
	 * The name (id) of this class for logging information.
	 */
	private static final String CLASS_ID = OtherFileDiff.class.getSimpleName();

	/**
	 * Construct a new {@link OtherFileDiff}.<br><br>
	 * 
	 * This constructor will call the super constructor of {@link FileDiff}, which will start a
	 * line-wise analysis of the given diff lines calling the inherited methods {@link #normalize(String, int)} and
	 * {@link #isVariabilityChange(String, int)} defined in this class.<br>
	 * Counting the number of changed lines (regardless of variability information) is done in {@link FileDiff}.
	 * 
	 * @param diffLines the lines of a model diff
	 * @param changesStartLineNum the index of the line in the given <code>diffLines</code> that
	 * marks the starting point of the change details in terms of added and removed lines
	 */
	public OtherFileDiff(String[] diffLines, int changesStartLineNum) {
		super(FileType.OTHER, diffLines, changesStartLineNum);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String normalize(String diffLine, int diffLinePosition) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isVariabilityChange(String cleanDiffLine, int cleanDiffLinePosition) {
		ComAnLogger.getInstance().log(CLASS_ID, "Change ignored", cleanDiffLine, MessageType.DEBUG);
		return false;
	}

}
