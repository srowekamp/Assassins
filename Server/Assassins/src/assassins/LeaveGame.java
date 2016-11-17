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
	public static final String RESULT_LEAVE_GAME_SUCCESS = "success";
	public static final String RESULT_NOT_IN_GAME = "not_in_game";
	public static final String RESULT_GAME_NOT_FOUND = "game_not_found";
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
		
		try {
        	gameID = request.getParameter(Game.KEY_GAMEID);
        	playerID = Integer.parseInt(request.getParameter(UserAccount.KEY_ID));
        } catch (Exception e) {
        	result = RESULT_PARAMETER_MISSING;
        	parameterMissing = true;
        }
		if(!parameterMissing) {
			if (DB.doesGameExist(gameID)) {
				game = DB.getGame(gameID);
				if(game != null) {
					int[] players = game.getPlayers();
					String playerList = "";
					for (int i = 0; i < players.length; i++) {
						if (players[i] == playerID) inGame = true;
						else playerList += String.format("%d,", players[i]);
					}
					if (inGame) {
						//update player list
						//update players_alive if game started
						//remove game from active_games if last player
					}
					else result = RESULT_NOT_IN_GAME;
				}
				else result = RESULT_ERROR;
			}
			else result = RESULT_GAME_NOT_FOUND;
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
