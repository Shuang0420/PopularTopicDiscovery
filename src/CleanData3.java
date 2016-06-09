import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.json.simple.parser.ParseException;

public class CleanData3 {

	// pls remember to replace  with "\" first in the original file, and also,
	// encoding the file with ""
	public static void main(String args[]) throws UnsupportedEncodingException, IOException, ParseException {
		// try (BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in, "utf-8"))) {
		String preId = "";
		ArrayList<String> preQuestion = new ArrayList<String>();
		StringBuilder preAnswer = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("output_sorted.txt"), "utf-8"));
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream("output8.txt"), "utf-8"))) {
			String str1 = "";
			String str;
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("\t");
				if (preId == null) {
					continue;
				}
				// System.out.println("i"+preId);
				if (!columns[0].equals(preId)) {
					// System.out.println("c"+columns[0]);
					// System.out.println(preQuestion.toString());
					if (preAnswer.toString().contains("DDDC") || preAnswer.toString().contains("zrg")) {
						// System.out.println("dddc");
						for (String i : preQuestion) {
							bw.write(preId + "\t" + i);
							bw.newLine();
						}
						preQuestion.clear();
						preAnswer = new StringBuilder();
					} else {
						preQuestion.clear();
						preAnswer = new StringBuilder();
					}
				}
				// else {
				// System.out.println("no");
				// }
				preId = columns[0];
				preQuestion.add(columns[4]);
				if (columns.length > 5) {
					// System.out.println("6");
					preAnswer.append(columns[5]);
					preAnswer.append("##");
				}
			}

		}
	}
}
