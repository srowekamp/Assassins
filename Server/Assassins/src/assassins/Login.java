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

public class Login extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = -6693642062446368134L;
	
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";

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

        Enumeration paramNames = request.getParameterNames();
        String params[] = new String[2];
        int i = 0;
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            params[i] = paramValues[0];
            i++;
 
        }
        String sql = "SELECT username, password FROM db309la05.users where username=? and password=?";
        Connection con = DBConnectionHandler.getConnection();
        String test = null;
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, params[0]);
            ps.setString(2, params[1]);
            ResultSet rs = ps.executeQuery();
            test = ps.toString();
            if (rs.next()) {
                jsonResponse.put("info", "success");
            } else {
                jsonResponse.put("info", "fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
