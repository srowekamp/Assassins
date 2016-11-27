package assassins;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class EndGame extends HttpServlet{
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 3390648842666208917L;
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_GAME_NOT_FOUND = "game_not_found"; // Result when no game was found with given name
	public static final String RESULT_GAME_NOT_STARTED = "game_not_started"; // Result when trying to end game that hasn't been started
	public static final String RESULT_ERROR = "error"; // Result when there was an unknown error
	public static final String RESULT_REMOVE_GAME_SUCCESS = "success"; // Result when the game was successfully ended
	public static final String RESULT_REMOVE_GAME_FAILURE = "fail"; // Result when there was an error removing game from database

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
		String result = null;
		String gameName = request.getParameter(Game.KEY_GAMEID);
		Game tempGame = null, game = null;

		// Check if the game exists
		if(DB.doesGameExist(gameName)){
			game = DB.getGame(gameName);
			// Check for error
			if (game != null) {
				// Check if game has been started
				if (game.getEndTime() != null && game.getEndTime().length() == 6) { 
					tempGame = DB.removeGame(game.getID(), gameName);
					// Check for error
					if (tempGame == null) {
						game.updateGamesPlayed();
						result = RESULT_REMOVE_GAME_SUCCESS;
						jsonResponse.put(Game.KEY_GAME, game);
					}
					else result = RESULT_REMOVE_GAME_FAILURE;
				}
				else result = RESULT_GAME_NOT_STARTED;
			}
			else result = RESULT_ERROR;
		}
		else result = RESULT_GAME_NOT_FOUND;
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
