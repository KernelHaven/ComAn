package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides some utility methods reused at several test classes.
 * 
 * @author Christian Kröher
 *
 */
public class TestUtils {

	/**
	 * Read the content of the given file and return a list of strings in which each string
	 * contains a single line of the content of the file.
	 * 
	 * @param file the {@link File} the content should be read from
	 * @return a {@link List} of {@link String}s representing the line-wise content of the
	 * given file; may return <code>null</code> if the given file cannot be read	
	 */
	public static List<String> readFile(File file) {
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
			System.out.println("[Error] exception while reading file \"" + file.getName() + "\": " + e.getMessage() + "\n");
		} finally {
			// Close the readers in any case
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					System.out.println("[Error] exception while closing file reader: " + e.getMessage());
					System.out.println("Error occured during reading file \"" + file.getName() + "\"\n");
				}
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					System.out.println("[Error] exception while closing buffered reader: " + e.getMessage());
					System.out.println("Error occured during reading file \"" + file.getName() + "\"\n");
				}
			}
		}
		return fileLines;
	}
}
