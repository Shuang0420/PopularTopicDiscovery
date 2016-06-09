import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Reducer {

	private static Connection conn = null;

	public static void main(String args[]) {
		conn = ConnectDB.getConnection();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
			// Initialize Variables
			String input;
			String word = null;
			String currentWord = null;
			int currentCount = 0;

			// While we have input on stdin
			while ((input = br.readLine()) != null) {
				try {
					String[] parts = input.split("\t");
					word = parts[0];
					int count = Integer.parseInt(parts[1]);

					// We have sorted input, so check if we
					// are we on the same word?
					if (currentWord != null && currentWord.equals(word))
						currentCount++;
					else // The word has changed
					{
						if (currentWord != null) {// Is this the first word, if
													// not
							// output count
//							System.out.println(currentWord + "\t" + currentCount);
							checkAndInsert(currentWord, currentCount);
						}
						currentWord = word;
						currentCount = count;
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}

			// Print out last word if missed
			if (currentWord != null && currentWord.equals(word)) {
//				System.out.println(currentWord + "\t" + currentCount);
				checkAndInsert(currentWord, currentCount);
			}

		} catch (IOException io) {
			io.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void checkAndInsert(String str, int count) throws SQLException {
		Statement stat = (Statement) conn.createStatement();
		String date = str.split("IamTokenizer")[0];
		String question = str.split("IamTokenizer")[1];
		
		if (question != null && !question.isEmpty())
			stat.executeUpdate("INSERT IGNORE INTO `FAQsys`.`FQcount` (`Question`, `Date`, `Count`) VALUES ('"
					+ question + "','" + date + "','" + count + "')");

		stat.close();
	}
}