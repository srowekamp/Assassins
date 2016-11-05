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
	public static final String RESULT_JOIN_GAME_SUCCESS = "joined game";
	public static final String RESULT_JOIN_GAME_FAILURE = "unable to join game";
	
	
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
		Game tempGame = null;
		boolean missingParameter = false;
		String result = null;
		try{
			tempGame = new Game(request);
		}
		catch(Exception e){
			missingParameter = true;
			result = RESULT_PARAMETER_MISSING;
		}
		Game game = null;
		if(!missingParameter){
			String validity = tempGame.checkValidity();
			if(validity.equals(Game.VALID)){
				if(DB.doesGameExist(tempGame.getGameID())){
					result = game.RESULT_GAME_EXISTS;
				}
				else{
					
				}
			}
		}
		
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
