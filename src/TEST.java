import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.json.simple.parser.ParseException;

public class TEST {
	public static void main(String args[]) throws UnsupportedEncodingException, IOException, ParseException {
		// try (BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in, "utf-8"))) {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("robotlog-512.txt"), "utf-8"));
				BufferedWriter bw = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"))) {
			String str;
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("\t", 5);
				columns[4] = columns[4].replace("\t", "##");
				columns[4] = columns[4].replace("\\s+", "##");
				columns[4] = columns[4].replace("\r", "##");
				bw.write(columns[0] + "\t" + columns[1] + "\t" + columns[2] + "\t" + columns[3] + "\t" + columns[4]);
				bw.newLine();

			}
		}

	}
}
