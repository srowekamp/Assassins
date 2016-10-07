package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import org.json.simple.JSONObject;
import assassins.CreateAccount;
import assassins.DBConnectionHandler;

public class Login extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = -6693642062446368134L;
	
	public static final String RESULT_LOGIN_SUCCESS = "success"; // Value of Result when user enters a valid login
	public static final String RESULT_LOGIN_FAIL = "fail"; // Value of Result when user enters an invalid login

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
        String username = request.getParameter(CreateAccount.KEY_USERNAME);
        String password = request.getParameter(CreateAccount.KEY_PASSWORD);
        
        if (!CreateAccount.isValidUsername(username)) jsonResponse.put(CreateAccount.KEY_RESULT, CreateAccount.RESULT_USERNAME_INVALID); // Check username and password for validity
        else if (!CreateAccount.isValidPassword(password)) jsonResponse.put(CreateAccount.KEY_RESULT, CreateAccount.RESULT_PASSWORD_INVALID);
        else {
        	// Check if the username and password provided exist in database
	        String sql = "SELECT username, password FROM db309la05.users where username=? and password=?";
	        Connection con = DBConnectionHandler.getConnection();
	        try {
	            PreparedStatement ps = con.prepareStatement(sql);
	            ps.setString(1, username);
	            ps.setString(2, password);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                jsonResponse.put(CreateAccount.KEY_RESULT, RESULT_LOGIN_SUCCESS);
	            } else {
	            	jsonResponse.put(CreateAccount.KEY_RESULT, RESULT_LOGIN_FAIL);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            jsonResponse.put(CreateAccount.KEY_RESULT, CreateAccount.RESULT_OTHER_ERROR);
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