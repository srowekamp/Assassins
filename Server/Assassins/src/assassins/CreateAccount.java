package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.UserAccount;
import assassins.DB;

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
	public static final String RESULT_NAME_INVALID = "name_error"; // Value of Result when an invalid real name is passed
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
        String username = request.getParameter(UserAccount.KEY_USERNAME);
        String password = request.getParameter(UserAccount.KEY_PASSWORD);
        String real_name = request.getParameter(UserAccount.KEY_REAL_NAME);
        String b64Image = request.getParameter(KEY_B64_JPG);
        UserAccount ua = null;
        
        // First check all provided parameters for validity. TODO make meaningful tests for validity
        if (!UserAccount.isValidUsername(username)) jsonResponse.put(KEY_RESULT, RESULT_USERNAME_INVALID); // Check username and password for validity
        else if (!UserAccount.isValidPassword(password)) jsonResponse.put(KEY_RESULT, RESULT_PASSWORD_INVALID);
        else if (!isValidImage(b64Image)) jsonResponse.put(KEY_RESULT, RESULT_IMAGE_INVALID);
        else if (!isValidRealName(real_name)) jsonResponse.put(KEY_RESULT, RESULT_NAME_INVALID);
        else {
        	// All provided parameters are valid, check to see if username is taken
        	if (!DB.doesUserExist(username)) {
        		// User does not exist, create new user
        		ua = DB.addUser(username, password, real_name);
        		if (ua == null) {
        			jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR);
        		}
        		else {
        			// Now save the provided image to the server and update the filename in the database
        			ua = DB.addUserImage(ua.getUserID(), b64Image);
        			if (ua == null) {
        				jsonResponse.put(KEY_RESULT, RESULT_OTHER_ERROR); // shouldn't happen
        				jsonResponse.put("img", b64Image);
        			}
        			else {
        				jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_CREATED);
        				jsonResponse.put(UserAccount.KEY_USER_ACCOUNT, ua.toJSONString());
        			}
        		}
	        }
        	else {
        		jsonResponse.put(KEY_RESULT, RESULT_ACCOUNT_EXISTS);
        	}
        }
        //Write the JSON object to the response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
    
    /** Returns true if the provided b64Image is valid */
    private boolean isValidImage(String b64Image) {
    	return (b64Image != null && b64Image.length() > 0);
    }
    
    /** Returns true if the provided b64Image is valid */
    private boolean isValidRealName(String real_name) {
    	return (real_name != null && real_name.length() > 0);
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
