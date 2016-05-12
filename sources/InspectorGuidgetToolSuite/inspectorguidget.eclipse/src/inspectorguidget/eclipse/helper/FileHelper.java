package inspectorguidget.eclipse.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileHelper {
	/**
	 * Append 'content' in 'dir'/'file'
	 */
	public static void appendFile(String file, String content) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			out.println(content);
			out.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}