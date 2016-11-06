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
	public static final String RESULT_JOIN_GAME_SUCCESS = "Joined game.";
	public static final String RESULT_JOIN_GAME_FAILURE = "Unable to join game. Password or GameID incorrect.";
	
	
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
		String playerList = request.getParameter(Game.KEY_PLAYERS_LIST);
		String gameID = request.getParameter(Game.KEY_GAMEID);
		String password = request.getParameter(Game.KEY_PASSWORD);
		Game tempGame = null;
		
		
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
