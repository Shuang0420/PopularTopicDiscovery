import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GetTopics {

	private static final String URL = "jdbc:mysql://127.0.0.1:3306/FAQsys?characterEncoding=UTF-8";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	private static Connection conn = null;

	/**
	 * Connect to database.
	 */
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return conn;
	}

	public static void insertIntotopics_v4() throws IOException, SQLException {
		Statement stat = (Statement) conn.createStatement();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("FAQ_cluster_v2.txt"), "utf-8"))) {
			// Initialize Variables
			String input;
			String docid = "";
			String score = "";
			String formula = "";
			String topic = "";

			// While we have input on stdin
			while ((input = br.readLine()) != null) {
				docid = input.split("\t")[0];
				score = input.split("\t")[1];
				formula = input.split("\t")[2];
				ResultSet rs = stat.executeQuery(
						"SELECT originalQuestion FROM `yibotDB`.`originalQuestion` WHERE id = '" + docid + "'");
				while (rs.next()) {
					topic = rs.getString("originalQuestion");
				}

				stat.executeUpdate(
						"INSERT IGNORE INTO `yibotDB`.`topics` (`docid`, `topic`, `score`, `formula`) VALUES ('" + docid
								+ "','" + topic + "','" + score + "','" + formula + "')");

			}
		}

	}

	public static void insertIntotopic_doc_v4() throws IOException, SQLException {
		Statement stat = (Statement) conn.createStatement();

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("topic_doc_v2.txt"), "utf-8"))) {
			// Initialize Variables
			String input;
			String docid = "";
			String score = "";
			String formula = "";

			// While we have input on stdin
			while ((input = br.readLine()) != null) {
				docid = input.split("\t")[0];
				score = input.split("\t")[1];
				formula = input.split("\t")[2];

				stat.executeUpdate("INSERT IGNORE INTO `yibotDB`.`topic_doc` (`docid`, `score`, `formula`) VALUES ('"
						+ docid + "','" + score + "','" + formula + "')");

			}
		}

	}

	public static ArrayList<String> getFile()
			throws SQLException, UnsupportedEncodingException, FileNotFoundException, IOException {
		ArrayList<String> result = new ArrayList<String>();
		Statement stat = (Statement) conn.createStatement();
		ResultSet rs = stat.executeQuery("SELECT * FROM FAQsys.Questions");
		while (rs.next()) {
			String question = rs.getString("Question");
			String str = rs.getString("originalQuestion");
			if (str.contains("##")) {
				String questions[] = str.split("##");
				for (String i : questions) {
					result.add(i + "##" + question);
				}
			} else {
				result.add(str+ "##" + question);
			}
		}
		return result;
	}

	public static void insertIntoOriginalQuestion() throws IOException, SQLException {
		int count = 1;
		Statement stat = (Statement) conn.createStatement();
		// getFile();
		ArrayList<String> text = getFile();
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("newfileWithIndex.txt"), "utf-8"))) {
			for (String i : text) {
				stat.executeUpdate(
						"INSERT IGNORE INTO `FAQsys`.`originalQuestion` (`originalQuestion`, `standardQuestion`) VALUES ('" + i.split("##")[0] + "','" + i.split("##")[1] + "')");
				bw.write(count + "\t");
				bw.write(i);
				bw.newLine();
				count++;

			}
		}
	}

	public static void main(String args[])
			throws SQLException, UnsupportedEncodingException, FileNotFoundException, IOException {
		conn = getConnection();
		// insertIntotopic_doc_v4();
		// insertIntotopics_v4();
		insertIntoOriginalQuestion();
	}

}
