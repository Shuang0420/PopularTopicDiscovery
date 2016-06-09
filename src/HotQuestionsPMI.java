import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HotQuestionsPMI {

	private static Connection conn = null;

	public static String startDay = "'2016-03-22'";// sysdate
	public static java.sql.Date dbDate = null;
	public static int currentDays = 0;

	public static HashMap<String, Double> questionWithCount = new HashMap<String, Double>();

	public static void main(String args[]) throws SQLException {
		conn = ConnectDB.getConnection();
		getPMI(1);
		getPMI(3);
		getPMI(7);
		getOutput(questionWithCount);

	}

	public static void getPMI(int intervalDays) throws SQLException {
		double PMI = 0;
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
			PMI = Math.log(a * (a + b + c + d)) / ((a + b) * (a + c));
			// only store latest max PMI
			if (!questionWithCount.containsKey(question) || questionWithCount.get(question) < PMI)
				questionWithCount.put(question, PMI);
		}

		stat.close();

	}

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
