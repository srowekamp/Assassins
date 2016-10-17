package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.simple.JSONObject;
import assassins.DBConnectionHandler;
import assassins.UserAccount;

public class CreateAccount extends HttpServlet {
	
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 3390648842666208917L;
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created
	public static final String RESULT_ACCOUNT_EXISTS = "exists"; // Value of Result when user with username provided already exists
	public static final String RESULT_USERNAME_INVALID = "username_error"; // Value of Result when user enters an invalid username
	public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
	public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();
        boolean accountExists = true;
        String username = request.getParameter(UserAccount.KEY_USERNAME);
        String password = request.getParameter(UserAccount.KEY_PASSWORD);
        
        if (!UserAccount.isValidUsername(username)) jsonResponse.put(KEY_RESULT, RESULT_USERNAME_INVALID); // Check username and password for validity
        else if (!UserAccount.isValidPassword(password)) jsonResponse.put(KEY_RESULT, RESULT_PASSWORD_INVALID);
        else {
        	// First check if the username provided already exists
	        String sql = "SELECT username, password FROM db309la05.users where username=?";
	        Connection con = DBConnectionHandler.getConnection();
	        try {
	            PreparedStatement ps = con.prepareStatement(sql);
	            ps.setString(1, username);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_EXISTS);
	            } else {
	                accountExists = false;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
	        }
	        if (!accountExists) {
	        	String sqlInsert = "INSERT INTO db309la05.users(username, password) VALUES (?, ?)";
	        	String sqlCheck = "SELECT username, password FROM db309la05.users where username=? and password=?";
	            try {
	            	// Next attempt to add the provided username and password to database
	                PreparedStatement psInsert = con.prepareStatement(sqlInsert);
	                psInsert.setString(1, username);
	                psInsert.setString(2, password);
	                psInsert.executeUpdate();
	                // Then check the database for the new entry
	                PreparedStatement psCheck = con.prepareStatement(sqlCheck);
	                psCheck.setString(1, username);
	                psCheck.setString(2, password);
	                ResultSet rsCheck = psCheck.executeQuery();
	                if (rsCheck.next()) {
	                    jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_CREATED);
	                } else {
	                	jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
	            }
	        }
        }
        //Write the JSON object to the response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
 
    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
