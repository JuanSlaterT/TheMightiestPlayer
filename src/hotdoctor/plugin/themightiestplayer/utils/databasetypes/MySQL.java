package hotdoctor.plugin.themightiestplayer.utils.databasetypes;


import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQL {

	private HikariDataSource hikari;
	private Connection connection;
	
	
	public MySQL(String Host, String Database, String User, String Password, Integer Port, String SSL) {
		hikari = new HikariDataSource();
		hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		hikari.addDataSourceProperty("serverName", Host);
        hikari.addDataSourceProperty("port", Port);
        hikari.addDataSourceProperty("databaseName", Database);
        hikari.addDataSourceProperty("user", User);
        hikari.addDataSourceProperty("password", Password);
        hikari.addDataSourceProperty("useSSL", SSL);

	}
	
	public void Connect() throws SQLException {
		connection = hikari.getConnection();
	}
	public Connection getConnection() {
		return connection;
	}
	
	public ResultSet executeQuery(String string) throws SQLException {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(string);
		return rs;
	}
	public void executeUpdate(String string) throws SQLException {
		Statement st = connection.createStatement();
		st.executeUpdate(string);
	}
	
	public boolean isConnected() throws SQLException {
		if(connection.isClosed()) {
			return false;
		}else {
			return true;
		}
	}
	
	
	public void Disconnect() throws SQLException {
		hikari.close();
	}
	


	
}
