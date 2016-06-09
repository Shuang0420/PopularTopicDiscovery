import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HotQuestions {

	private static Connection conn = null;

	public static String startDay = "'2016-03-22'";// sysdate
	public static java.sql.Date dbDate = null;
	public static int currentDays = 0;
	public static final double notExceedBefore = 0.2;
	public static final double exceedAfter = 0.2;

	public static HashMap<String, Double> questionWithCount = new HashMap<String, Double>();

	public static void main(String args[]) throws SQLException {
		conn = ConnectDB.getConnection();

		getChiSquare(1);
		getChiSquare(3);
		getChiSquare(7);
		insertIntoFQrank();
		getOutput(questionWithCount);
	}

	/**
	 * Compute Chi-square
	 * 
	 * @param intervalDays
	 * @throws SQLException
	 */
	public static void getChiSquare(int intervalDays) throws SQLException {
		double chiSquare = 0;
		double a = 0;// includeAfter
		double b = 0;// excludeAfter
		double c = 0;// includeBefore
		double d = 0;// excludeBefore

		double allQuestionBefore = 0;
		double allQuestionAfter = 0;

		// this query is only for the latest hot questions
		String query = "SELECT * FROM ((SELECT IFNULL(question,'TOTAL') question, SUM(adjustedCount) countBefore FROM FAQsys.FQtable WHERE Date <= date_sub("
				+ startDay + ", interval '" + intervalDays
				+ "' day) GROUP BY question WITH ROLLUP) AS A JOIN (SELECT IFNULL(question,'TOTAL') question, SUM(adjustedCount) countAfter FROM FAQsys.FQtable WHERE Date >=date_sub("
				+ startDay + ", interval '" + intervalDays
				+ "' day) GROUP BY question WITH ROLLUP) AS B USING(question))";

		Statement stat = (Statement) conn.createStatement();
		ResultSet rs = stat.executeQuery(query + "WHERE question = 'TOTAL'");

		// get total count for questions for a given day
		while (rs.next()) {
			allQuestionBefore = rs.getDouble("countBefore");
			allQuestionAfter = rs.getDouble("countAfter");
		}

		// this query is for any time point hot question
		String query2 = "SELECT * FROM ((SELECT IFNULL(question,'TOTAL') question, SUM(adjustedCount) countBefore FROM FAQsys.FQtable WHERE Date <= date_sub("
				+ startDay + ", interval '" + intervalDays
				+ "' day) GROUP BY question WITH ROLLUP) AS A JOIN (SELECT IFNULL(question,'TOTAL') question, SUM(adjustedCount) countAfter FROM FAQsys.FQtable WHERE Date >=date_sub("
				+ startDay + ", interval '" + intervalDays + "' day) AND Date <=" + startDay
				+ " GROUP BY question WITH ROLLUP) AS B USING(question))";

		ResultSet rs2 = stat.executeQuery(query2);

		// get every question's chi-square for a given day
		while (rs2.next() && !rs2.getString("question").equals("TOTAL")) {
			String question = rs2.getString("question");
			c = rs2.getDouble("countBefore");
			a = rs2.getDouble("countAfter");
			d = allQuestionBefore - a;
			b = allQuestionAfter - c;
			chiSquare = (Math.pow(a * d - b * c, 2)) / ((a + b) * (a + c) * (b + c) * (b + d));
			// only store latest max chiSquare
			if (!questionWithCount.containsKey(question) || questionWithCount.get(question) < chiSquare)
				questionWithCount.put(question, chiSquare);
		}

		stat.close();

	}

	public static void getCurrentDays() throws SQLException {
		Statement stat = (Statement) conn.createStatement();
		ResultSet rs = stat.executeQuery("SELECT COUNT(DISTINCT date) days FROM FAQsys.FQtable");
		while (rs.next()) {
			currentDays = rs.getInt("days");
		}
		ResultSet rs2 = stat.executeQuery("SELECT MAX(date) maxDate FROM FAQsys.FQtable");
		while (rs2.next()) {
			dbDate = rs2.getDate("maxDate");
		}
		stat.close();
	}

	public static void insertIntoFQrank() throws SQLException {
		Statement stat = (Statement) conn.createStatement();
		stat.executeUpdate("TRUNCATE `FAQsys`.`FQrank`");
		Iterator<Entry<String, Double>> iterator = questionWithCount.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Double> entry = iterator.next();
			stat.executeUpdate("INSERT INTO `FAQsys`.`FQrank` (`Question`, `Chisquare`) VALUES ('" + entry.getKey()
					+ "','" + entry.getValue() + "')");
		}

		stat.close();
	}

	/**
	 * print output, hot asked questions recently, in descending order
	 * 
	 * @param questionWithCount2
	 */
	public static void getOutput(HashMap<String, Double> questionWithCount2) {
		List<Map.Entry<String, Double>> newList = new ArrayList<Map.Entry<String, Double>>(
				questionWithCount2.entrySet());

		// 排序
		Collections.sort(newList, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});
		System.out.println("================================I AM OUTPUT================================");
		for (int i = 0; i < newList.size(); i++) {
			Entry<String, Double> ent = newList.get(i);
			System.out.println(ent.getKey() + "=" + ent.getValue());

		}
	}

}
