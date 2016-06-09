import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
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

	public static void closeConnection() throws SQLException {
		conn.close();
	}

}
