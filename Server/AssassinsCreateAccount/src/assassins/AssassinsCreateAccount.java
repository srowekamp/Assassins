package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.Enumeration;
import org.json.simple.JSONObject;

import assassins.DBConnectionHandler;

public class AssassinsCreateAccount extends HttpServlet {
	
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 3390648842666208917L;
	
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created
	public static final String RESULT_ACCOUNT_EXISTS = "exists"; // Value of Result when user with username provided already exists
	public static final String RESULT_OTHER_ERROR = "fail"; // Value of Result when an error occurs

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
        /*Enumeration paramNames = request.getParameterNames();
        String params[] = new String[2];
        int i = 0;
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            params[i] = paramValues[0];
            i++;
 
        }*/
        String username = request.getParameter(KEY_USERNAME);
        String password = request.getParameter(KEY_PASSWORD);
        if (username == null || password == null) jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
        String sql = "SELECT username, password FROM db309la05.users where username=?";
        Connection con = DBConnectionHandler.getConnection();
        //String test = null;
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            //test = ps.toString();
            if (rs.next()) {
                jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_EXISTS);
            } else {
                accountExists = false;
                //jsonResponse.put("test", "test1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //jsonResponse.put("test", "test2");
        }
        if (!accountExists) {
        	String sqlInsert = "INSERT INTO db309la05.users(username, password) VALUES (?, ?)";
        	String sqlCheck = "SELECT username, password FROM db309la05.users where username=? and password=?";
        	Connection conInsert = DBConnectionHandler.getConnection();
        	Connection conCheck = DBConnectionHandler.getConnection();
            //String test = null;
            try {
                PreparedStatement psInsert = conInsert.prepareStatement(sqlInsert);
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.executeUpdate();
                PreparedStatement psCheck = conCheck.prepareStatement(sqlCheck);
                psCheck.setString(1, username);
                psCheck.setString(2, password);
                ResultSet rsCheck = psCheck.executeQuery();
                //test = ps.toString();
                if (rsCheck.next()) {
                    jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_CREATED);
                } else {
                	jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //jsonResponse.put("test", "test3");
            }
        }
        
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
