package assassins;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.simple.JSONObject;

public class LeaveGame extends HttpServlet{
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 3390648842666208917L;
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_LEAVE_GAME_SUCCESS = "success"; // The player was successfully removed from the list of players in game
	public static final String RESULT_NOT_IN_GAME = "not_in_game"; // The player was not in the game
	public static final String RESULT_GAME_NOT_EXIST_OR_END = "game_not_exist_or_end"; // Result when the game wasn't found in the database. Either it DNE or it was ended
	public static final String RESULT_PLAYER_DEAD = "dead-success"; // Player can safely leave game when dead. That way they still get gamesPlayed++ when game ends
	public static final String RESULT_PLAYER_WON = "win"; // Player was the last one alive, don't let them leave. Wait for their device to call EndGame.
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
			throws ServletException, IOException{
		JSONObject jsonResponse = new JSONObject();
		boolean parameterMissing = false;
		String result = null;
		int playerID = -1;
		String gameID = null;
		String password = null;
		Game game = null;
		boolean inGame = false;
		boolean t = true; // Variable to store whether or not to remove the player from the players_list in database
		
		try {
        	gameID = request.getParameter(Game.KEY_GAMEID);
        	playerID = Integer.parseInt(request.getParameter(UserAccount.KEY_ID));
        } catch (Exception e) {
        	result = RESULT_PARAMETER_MISSING;
        	parameterMissing = true;
        }
		if(!parameterMissing) {
			// check if the game name is valid
			if (DB.doesGameExist(gameID)) {
				game = DB.getGame(gameID);
				// check for error
				if(game != null) {
					// check if game is started (if game is started and player is dead, they can leave without removing their id from players_list
					if (game.isStarted()) {
						// check if the player is alive
						if (game.isPlayerAlive(playerID)) {
							// if there are there other players alive, remove the player from players_alive
							if (game.getPlayersAlive().length > 1) {
								game = game.killPlayer(playerID);
							}
							// if the player is the last one alive, they won.
							else {
								result = RESULT_PLAYER_WON;
								t = false;
							}
						}
						else {
							result = RESULT_PLAYER_DEAD;
							t = false;
						}
					}
					// The game isn't started or player left while in progress, so remove player from players_list
					if (t) {
						// Build a new players_list
						int[] players = game.getPlayers();
						String playersList = "";
						for (int i = 0; i < players.length; i++) {
							if (players[i] == playerID) inGame = true;
							else playersList += String.format("%d,", players[i]);
						}
						// Check if player is in the game
						if (inGame) {
							//update players list in database
							game = DB.updatePlayersList(game, playersList);
							// check for error
							if (game != null) {
								//remove game from active_games if last player
								if (game.getPlayers() == null || game.getPlayers().length == 0) {
									DB.removeGame(game.getID(), game.getGameID());
								}
								result = RESULT_LEAVE_GAME_SUCCESS;
							}
							else result = RESULT_ERROR;
						}
						else result = RESULT_NOT_IN_GAME;
					}
				}
				else result = RESULT_ERROR;
			}
			else result = RESULT_GAME_NOT_EXIST_OR_END;
		}
		else result = RESULT_PARAMETER_MISSING;
		jsonResponse.put(KEY_RESULT, result);
		/* write the json object to the response */
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
    		throws ServletException, IOException{
    	doGet(request, response);
    }

}
