import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.hadoop.io.Text;

public class Mapper {

	public static void main(String args[]) throws UnsupportedEncodingException, IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"))) {
			String str;
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("\t");
				if (columns.length >= 3) {
					String date = columns[0].substring(0, 10);
					int index = columns[2].indexOf('，');
					String question = columns[1];
					System.out.println(date + "IamTokenizer" + question + "\t"  + "1");
				}
			}

		}

	}

}
