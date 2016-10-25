package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.UserAccount;
import assassins.DB;

public class CreateGame extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 1788356290670041061L;

	public static final String KEY_RESULT = "result";
	
	public static final String RESULT_GAME_CREATED = "success"; // Value of Result when game successfully created
	public static final String RESULT_GAME_EXISTS = "exists"; // Value of Result when game with the name provided already exists
	public static final String RESULT_GAMEID_INVALID = "gameid_error"; // Value of Result when user enters an invalid gameID/name
	public static final String RESULT_PASSWORD_INVALID = "password_error"; // Value of Result when user enters an invalid password
	public static final String RESULT_CENTER_INVALID = "center_error"; // Value of Result when either center coordinate is not valid
	public static final String RESULT_RADIUS_INVALID = "radius_error"; // Value of Result when an invalid radius is passed
	public static final String RESULT_HOSTID_INVALID = "hostid_error"; // Value of Result when an invalid host ID is passed
	public static final String RESULT_DURATION_INVALID = "duration_error"; // Value of Result when an invalid duration is passed
	public static final String RESULT_OTHER_ERROR = "other_error"; // Value of Result when an error occurs
	
	public static final String KEY_GAMEID = "gameid"; // Name of the game that users will see in game
	public static final String KEY_PASSWORD = "password"; // Password to enter a private (default) game
	public static final String KEY_X_CENTER = "xcenter"; // Longitude of the game area
	public static final String KEY_Y_CENTER = "ycenter"; // Latitude of the game area
	public static final String KEY_RADIUS = "radius"; // Radius of the game area in meters
	public static final String KEY_HOSTID = "hostid"; // Integer id of the user who created the game
	public static final String KEY_DURATION = "duration"; // Duration of the game in seconds 

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
        JSONObject jsonResponse	= new JSONObject();
        
        String gameID 			= request.getParameter(KEY_GAMEID);
        String password 		= request.getParameter(KEY_PASSWORD);
        double xcenter 			= Double.parseDouble(request.getParameter(KEY_X_CENTER));
        double ycenter 			= Double.parseDouble(request.getParameter(KEY_Y_CENTER));
        String radius 			= request.getParameter(KEY_RADIUS);
        int hostID 				= Integer.parseInt(request.getParameter(KEY_HOSTID));
        int duration  			= Integer.parseInt(request.getParameter(KEY_DURATION));
        
        // First check all provided parameters for validity. TODO make meaningful tests for validity
        if (!isValidGameID(gameID)) jsonResponse.put(KEY_RESULT, RESULT_GAMEID_INVALID); // Check gameid for validity
        else {
        	// All provided parameters are valid, check to see if gameID is taken
        	if (!DB.doesGameExist(gameID)) {
        		// Game does not exist; create the new game
        		
	        }
        	else {
        		jsonResponse.put(KEY_RESULT, RESULT_GAME_EXISTS);
        	}
        }
        //Write the JSON object to the response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
    }
    
    /** Returns true if the provided b64Image is valid */
    private boolean isValidGameID(String gameID) {
    	return (gameID != null && gameID.length() > 0);
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
