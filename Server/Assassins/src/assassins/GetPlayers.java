package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.UserAccount;
import assassins.DB;

public class GetPlayers extends HttpServlet {
	
    /**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = -8266407644996434044L;
	
	public static final String KEY_RESULT = "result";
	public static final String KEY_NUM_PLAYERS = "num_players";
	
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_NORMAL = "normal";
	public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur

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
        String result = null;
        boolean parameterMissing = false;
        String gameID = null;
        int playerID = -1;
        double xlocation = -1;
        double ylocation = -1;
        try {
        	gameID = request.getParameter(Game.KEY_GAMEID);
        	playerID = Integer.parseInt(request.getParameter(UserAccount.KEY_ID));
        	xlocation = Double.parseDouble(request.getParameter(UserAccount.KEY_X_LOCATION));
        	ylocation = Double.parseDouble(request.getParameter(UserAccount.KEY_Y_LOCATION));
        } catch (Exception e) {
        	result = RESULT_PARAMETER_MISSING;
        	parameterMissing = true;
        }
        if (!parameterMissing) { // TODO ensure values grabbed from request are valid
        	// Get the latest game object
        	Game game = DB.getGame(gameID);
        	if (game == null) {
        		result = RESULT_ERROR;
        	}
        	else {
        		// Get the list of player ids
        		int[] players = game.getPlayers();
        		// TODO ensure list is valid
        		int numPlayers = players.length;
        		jsonResponse.put(KEY_NUM_PLAYERS, numPlayers);
        		// Add each player to the JSONObject response with Key: "Player %d" starting from 0 to numPlayers - 1
        		if (numPlayers > 0) {
        			for (int i = 0; i < numPlayers; i++) {
        				jsonResponse.put(String.format("Player %d", i), DB.getUser(players[i]));
        			}
        			// Update the user's location so that the lobby can display the location of all in game while waiting for game to start
        			DB.updateUserLocation(playerID, xlocation, ylocation);
        			jsonResponse.put(Game.KEY_GAME, DB.getGame(gameID));
        			result = RESULT_NORMAL;
        		}
        		else {
	        		result = RESULT_ERROR;
        		}
        	}
        }
        jsonResponse.put(KEY_RESULT, result);
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