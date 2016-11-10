package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.Game;

/** Called every X seconds by all players in a game. Clients send their latest GPS location and receive the latest relevant game info */
public class UpdateGame extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 4260384651788716605L;
	public static final String KEY_RESULT = "result";
	
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_PLAYER_DEAD = "dead"; // Result when the player was killed since the last update
	public static final String RESULT_NORMAL = "normal"; // Result when the game is proceeding as normal
	public static final String RESULT_GAME_WIN = "win"; // Result when the player has won the game
	public static final String RESULT_GAME_OVER = "game_over"; // Result when the game has ended because time ran out or host ended game early
	public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur
	
	public static final String KEY_TARGET = "target"; // Key in the JSONObject response corresponding to the player's target represented by a JSONObject in String form
	public static final String KEY_IS_TOP = "istop"; // Key in the JSONObject response representing whether or not the player is at the top of the AlivePlayers list

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
        boolean parameterMissing = false;
        String result = null;
        String gameID = null;
        int playerID = -1;
        double xlocation = -1;
        double ylocation = -1;
        boolean isTop = false;
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
        	// Check that the player is still alive
        	else if (!game.isPlayerAlive(playerID)) {
        		result = RESULT_PLAYER_DEAD;
        	}
        	else {
        		// Get the player's target
        		UserAccount target = game.getTarget(playerID);
        		if (target == null) {
        			result = RESULT_ERROR;
        		}
        		else {
	        		/* Check if the player is at the top of the AlivePlayers list.
	        		   The player at the top will make the request to End the game when time is up or all other players are dead*/
	        		isTop = game.isTop(playerID);
	        		// Add this status to the response
	        		jsonResponse.put(KEY_IS_TOP, isTop);
	        		// If the player's target is the player and they are at the top of AlivePlayers, they have won
	        		if (isTop && target.getUserID() == playerID) {
	        			result = RESULT_GAME_WIN;
	        		}
	        		else if (game.getEndTime().equals(Game.GAME_OVER)) {
	        			result = RESULT_GAME_OVER;
	        		}
	        		else {
	        			// The game is proceeding as normal, first update the player's location in the database
	        			DB.updateUserLocation(playerID, xlocation, ylocation);
	        			// Now put the player's target in the response JSONObject
	        			jsonResponse.put(KEY_TARGET, target);
	        			// Also put the game in the response
	        			jsonResponse.put(Game.KEY_GAME, game);
	        			result = RESULT_NORMAL;
	        		}
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
