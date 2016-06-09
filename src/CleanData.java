import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CleanData {
	
	
	//pls remember to replace  with "\" first in the original file, and also, encoding the file with ""
	public static void main(String args[]) throws UnsupportedEncodingException, IOException, ParseException {
		// try (BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in, "utf-8"))) {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("robotlog-512.txt"), "utf-8"));
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream("output4.txt"), "utf-8"))) {
			String str;
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("\t", 6);
				if (columns.length > 0) {
					//System.out.println(columns[4]);
					if (columns[5].contains("info=[") && !columns[5].endsWith("\"")) {
						//System.out.println("yes");
						int index = columns[5].indexOf("info=[", 1);
						String jsonstr = columns[5].substring(index + 5, columns[5].length() - 1);
						jsonstr = jsonstr.replaceAll("\\\t", "#");
						jsonstr = jsonstr.replaceAll("\\\\n", "");
						//jsonstr = jsonstr.replaceAll("<p class=\"p1\"><span class=\"s1\">", "");
						//System.out.println(jsonstr);
						// System.out.println("[jsonstr]" + jsonstr);
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(jsonstr);
						JSONArray jsonArray = (JSONArray) obj;
						Iterator iterator = jsonArray.iterator();
						String answer = "";
						while (iterator.hasNext()) {
							Object obj2 = parser.parse(iterator.next().toString());
							JSONObject jsonObject2 = (JSONObject) obj2;
							String tmp = (String) (jsonObject2).get("answer");
							//System.out.println(tmp);
							if (tmp==null || tmp.isEmpty()) {
								tmp = "null";
							}
							answer = answer + tmp + "##";
						}
						bw.write(
								columns[0] + "\t" + columns[1] + "\t" + columns[2] + "\t" + columns[3] + "\t" + columns[4] + "\t" + answer);
						bw.newLine();
					}
					else {
						bw.write(str);
						bw.newLine();
					}
				}

			}
		}
	}

}
