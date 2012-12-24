package cz.mtrakal.inpda_sem.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionModel {

	Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public ConnectionModel() throws SQLException {
		connect();
	}

	private void connect() throws SQLException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "mtrakal", "mtrakal");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void finalize() throws Throwable {
		connection.close();
		super.finalize();
	}
}
