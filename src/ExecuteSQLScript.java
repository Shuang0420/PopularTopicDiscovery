import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ExecuteSQLScript {
	private static Connection conn = null;
	
	public static void main (String[] args) throws UnsupportedEncodingException, IOException, SQLException {
		conn = ConnectDB.getConnection();
		updateCount();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"))) {
			String str;
			while ((str = br.readLine()) != null) {
				String[] columns = str.toString().split("##",3);
				if (columns.length > 0) {
					String date = columns[0];
					String question = columns[1];
					String originalQuestion = columns[2];
					//System.out.println(date + "IamTokenizer" + question + "\t"  + "1");
					insert(date,question,originalQuestion);
				}
			}

		}
	}
		
		public static void insert(String date,String question, String originalQuestion) throws SQLException {
			Statement stat = (Statement) conn.createStatement();
			originalQuestion = originalQuestion.replaceAll("'", "\"");
			if (question != null && !question.isEmpty())
				stat.executeUpdate("INSERT IGNORE INTO `FAQsys`.`Questions` (`Date`,`Question`, `originalQuestion`) VALUES ('"
						+ date + "','" + question + "','" + originalQuestion + "')");

			stat.close();
		}
		
		
		/**
		 * update FQ_adjustedCount which contains the adjusted count
		 * @throws SQLException
		 */
		// can be optimized by adding data instead of truncate and insert. use where data > sysdate when update
		public static void updateCount() throws SQLException {
			Statement stat = (Statement) conn.createStatement();
//			stat.executeUpdate("truncate `FAQsys`.`FQ_adjustedCount`");
			stat.executeUpdate(
					//"INSERT IGNORE INTO `FAQsys`.`FQ_adjustedCount` SELECT question,date,Count/dayCount FROM ((SELECT date,SUM(Count) dayCount FROM `FAQsys`.`FQcount` GROUP BY Date) AS A LEFT JOIN `FAQsys`.`FQcount` USING (date))");
					"INSERT IGNORE INTO `FAQsys`.`FQtable` SELECT question,date,Count,dayCount,Count/dayCount FROM ((SELECT date,SUM(Count) dayCount FROM `FAQsys`.`FQcount` GROUP BY Date) AS A LEFT JOIN `FAQsys`.`FQcount` USING (date))");
		}
		
	}



