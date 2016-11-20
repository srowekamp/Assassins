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
	public static final String KEY_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_REMOVE_GAME_SUCCESS = "success";
	public static final String RESULT_REMOVE_GAME_FAILURE = "fail";

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
		
		// TODO check if game has been started
		if(DB.doesGameExist(gameName)){
			game = DB.getGame(gameName);
			tempGame = DB.removeGame(game.getID(), gameName);
			/*
			 * update stats for all players when stats exist
			 */
			if (tempGame == null) {
				game.updateGamesPlayed();
				result = RESULT_REMOVE_GAME_SUCCESS;
				jsonResponse.put(Game.KEY_GAME, game);
			}
			else {
				result = RESULT_REMOVE_GAME_FAILURE;
			}
		}
		else {
			result = RESULT_REMOVE_GAME_FAILURE;
		}
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
