package assassins;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 
 
public class DBConnectionHandler {
 
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://mysql.cs.iastate.edu/db309la05";
	
	// Database credentials
	static final String USER = "dbu309la05";
	static final String PASS = "z8ndHcbY7wj";
	
    Connection con = null;
 
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName(JDBC_DRIVER); //Mysql Connection
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS); //mysql database
 
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}