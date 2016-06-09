import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CleanData2 {

	// pls remember to replace  with "\" first in the original file, and also,
	// encoding the file with ""
	public static void main(String args[]) throws UnsupportedEncodingException, IOException, ParseException {
		// try (BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in, "utf-8"))) {
		String preStr = "";
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("output_sorted.txt"), "utf-8"));
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream("output7.txt"), "utf-8"))) {
			String str1 = "";
			String str;
			String tmpAft = "";
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("\t");
				// System.out.println(columns[4]);
				// tmpAft=columns[0]+"\t"+columns[4];
				if (columns.length > 0) {
					// System.out.println(columns[4]);
//					if (str1 == null || str.isEmpty()) {
//						continue;
//					}
					if (preStr == null) {
						continue;
					}
					bw.write(preStr + "\t" + str);
					// bw.write(
					// tmpAft + "\t" + str1);
					bw.newLine();
					str1 = str;
				} else {
					bw.write(preStr + "\t" + str);
					// bw.write(tmpAft+"\t"+str1);
					bw.newLine();
				}
				preStr = columns[0] + "\t" + columns[4];
			}

		}
	}
}
