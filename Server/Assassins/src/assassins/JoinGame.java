package assassins;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.simple.JSONObject;

public class JoinGame extends HttpServlet{
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 3390648842666208917L;
	
	public static final String KEY_RESULT = "result";
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_JOIN_GAME_SUCCESS = "success";
	public static final String RESULT_JOIN_GAME_FAILURE = "fail";
	
	
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
		String playerID = request.getParameter(UserAccount.KEY_ID);
		String gameID = request.getParameter(Game.KEY_GAMEID);
		String password = request.getParameter(Game.KEY_PASSWORD);
		Game tempGame = null;
		
		// TODO add null pointer protection and make sure playerID is valid
		// TODO check if player is already in game
		if(DB.doesGameExist(gameID)){
			tempGame = DB.attemptJoinGame(gameID, password);
			if(tempGame != null){
				int[] players = tempGame.getPlayers();
				String playerList = "";
				/* turns array into string */
				for(int i = 0; i < players.length; i++){
					playerList += String.format("%d,", players[i]);
				}
				playerList += playerID + ",";
				tempGame = DB.joinGame(tempGame, playerList);
				if (tempGame != null) {
					result = RESULT_JOIN_GAME_SUCCESS;
					jsonResponse.put(Game.KEY_GAME, tempGame);
				}
				else result = RESULT_JOIN_GAME_FAILURE;
			}
			else result = RESULT_JOIN_GAME_FAILURE;
		}
		else result = RESULT_JOIN_GAME_FAILURE;
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