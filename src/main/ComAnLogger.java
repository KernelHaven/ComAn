package main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to print different types of information to the console.
 * 
 * @author Christian Kroeher
 *
 */
public class ComAnLogger {

	/**
	 * Singleton instance of this class.
	 */
	private static ComAnLogger instance = new ComAnLogger();

	/**
	 * This enumeration defines the different types of messages, this class
	 * differentiates:
	 * <ul>
	 * <li>INFO: Information about current status of the tool (always
	 * displayed)</li>
	 * <li>WARNING: Information about problems that do not influence the overall
	 * execution of the tool (only displayed if warnings are enabled)</li>
	 * <li>ERROR: Information about problems that force stopping the overall
	 * execution of the tool (always displayed)</li>
	 * <li>DEBUG: Information about individual process steps used for debugging
	 * (only displayed if debug is enabled)</li>
	 * </ul>
	 * 
	 * @author Christian Kroeher
	 *
	 */
	public enum MessageType {
		INFO, WARNING, ERROR, DEBUG
	};

	/**
	 * The option for enabling warnings. By default, these warnings are disabled.
	 */
	private boolean warningsEnabled;

	/**
	 * The option for enabling debug information. By default, these information are
	 * disabled.
	 */
	private boolean debugEnabled;

	/**
	 * Construct a new {@link ComAnLogger}.
	 */
	private ComAnLogger() {
		warningsEnabled = false;
		debugEnabled = false;
	}

	/**
	 * Return the single instance of the {@link ComAnLogger}.
	 * 
	 * @return the single instance of the {@link ComAnLogger}
	 */
	public static ComAnLogger getInstance() {
		return instance;
	}

	/**
	 * Enable printing warnings to the console. There is no way of disabling again.
	 */
	public void enableWarnings() {
		warningsEnabled = true;
	}

	/**
	 * Enable printing debug information to the console. There is no way of
	 * disabling again.
	 */
	public void enableDebug() {
		debugEnabled = true;
	}

	/**
	 * Print the given information to the console.
	 * 
	 * @param origin
	 *            the name of the class calling this method
	 * @param message
	 *            the message to be displayed
	 * @param description
	 *            optional description, can be <code>null</code>
	 * @param type
	 *            the {@link MessageType} of this message
	 */
	public void log(String origin, String message, String description, MessageType type) {
		if (message == null) {
			message = "";
		}
		
		if (origin != null) {
			message += ": " + origin;
		}
		
	    if (description != null) {
	    	message += "\n" + description;
	    }
	    
		
		if ((warningsEnabled || (!warningsEnabled && type != MessageType.WARNING))
				&& (debugEnabled || (!debugEnabled && type != MessageType.DEBUG))) {
			Logger logger;
			logger = Logger.getGlobal();
			
			if (MessageType.INFO.equals(type)) {
				logger.log(Level.INFO, message);
			} else if (MessageType.WARNING.equals(type)) {
				logger.log(Level.WARNING, message);
			} else if (MessageType.ERROR.equals(type)) {
				logger.log(Level.SEVERE, message);
			} else if (MessageType.DEBUG.equals(type)) {
				logger.log(Level.FINE, message);
			}
		}
	}
}
