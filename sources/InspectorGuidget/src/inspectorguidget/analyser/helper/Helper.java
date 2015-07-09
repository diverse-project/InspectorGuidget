package inspectorguidget.analyser.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Helper {
	/**
	 * Write 'content' in 'dir'/'file'
	 */
	public static void writeFile(String dir, String file, String content) {
		File newDir = new File(dir);
		newDir.mkdirs();

		try (FileWriter fw = new FileWriter(dir + "/" + file, false); BufferedWriter output = new BufferedWriter(fw);) {
			output.write(content);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if o is in list with == operator
	 */
	public static boolean identityContains(Object o, List<?> list) {
		for (Object l : list) {
			if (o == l)
				return true;
		}
		return false;
	}
}
