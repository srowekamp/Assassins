package assassins;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	
	public static final String KEY_B64_JPG = "b64_jpg";
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_ACCOUNT_CREATED = "success"; // Value of Result when account successfully created
	public static final String RESULT_ACCOUNT_EXISTS = "exists"; // Value of Result when user with username provided already exists
	public static final String RESULT_USERNAME_INVALID = "username_error"; // Value of Result when user enters an invalid username
	public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
	public static final String RESULT_IMAGE_INVALID = "image_error"; // Value of Result when an invalid Base64 encoded image is passed
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
        String real_name = request.getParameter(UserAccount.KEY_REAL_NAME);
        String image_filename = "0.jpg";
        String b64Image = request.getParameter(KEY_B64_JPG);
        
        if (!UserAccount.isValidUsername(username)) jsonResponse.put(KEY_RESULT, RESULT_USERNAME_INVALID); // Check username and password for validity
        else if (!UserAccount.isValidPassword(password)) jsonResponse.put(KEY_RESULT, RESULT_PASSWORD_INVALID);
        else if (!isValidImage(b64Image)) jsonResponse.put(KEY_RESULT, RESULT_IMAGE_INVALID);
        else {
        	// First check if the username provided already exists
	        String sql = "SELECT username FROM db309la05.users3 where username=?";
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
	        	String sqlInsert = "INSERT INTO db309la05.users3(username, password, real_name, image_filename, total_kills, games_played)"
	        			+ " VALUES (?, ?, ?, ?, ?, ?)";
	        	
	        	String sqlCheck = "SELECT * FROM db309la05.users3 where username=? and password=?";
	        	
	            UserAccount ua = null;
                ResultSet rsCheck = null;
	            try {
	            	// Next attempt to add the provided data to database
	                PreparedStatement psInsert = con.prepareStatement(sqlInsert);
	                psInsert.setString(1, username);
	                psInsert.setString(2, password);
	                psInsert.setString(3, real_name);
	                psInsert.setString(4, image_filename);
	                psInsert.setInt(5, 0);
	                psInsert.setInt(6, 0);
	                psInsert.executeUpdate();
	                updateUserImage(username, password, b64Image); // TODO implement image receive
	                // Then check the database for the new entry
	                PreparedStatement psCheck = con.prepareStatement(sqlCheck);
	                psCheck.setString(1, username);
	                psCheck.setString(2, password);
	                rsCheck = psCheck.executeQuery();
	                if (rsCheck.next()) {
	                    jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_CREATED);
	                    ua = new UserAccount(rsCheck);
	    	        	jsonResponse.put(UserAccount.KEY_USER_ACCOUNT, ua.toJSONString());
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
    
    public boolean updateUserImage(String username, String password, String b64Image) {
    	int id = 0;
    	String sql = "SELECT * FROM db309la05.users3 where username=? and password=?";
        Connection con = DBConnectionHandler.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
            	id = rs.getInt(UserAccount.KEY_ID);
            } else {
            	return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sqlUpdate = "UPDATE db309la05.users3 SET image_filename=? WHERE id=?";
        try {
            PreparedStatement ps = con.prepareStatement(sqlUpdate);
            String newFileName = (new Integer(id).toString()) + ".jpg";
            ps.setString(1, newFileName);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.getString(UserAccount.KEY_IMAGE_PATH).equals(newFileName)) {
            	if (saveB64Image(newFileName, b64Image)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return false;
    }
    
    private boolean saveB64Image(String filename, String b64Image) {
    	String filepath = "/var/lib/tomcat/webapps/userImages/";
    	byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(b64Image);
    	try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File outputImage = new File(filepath + filename);
	    	ImageIO.write(img, "jpg", outputImage);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    private boolean isValidImage(String b64Image) {
    	return (b64Image != null && b64Image.length() > 0);
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
