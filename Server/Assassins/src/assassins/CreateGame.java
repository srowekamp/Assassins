package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.Game;

public class CreateGame extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 1788356290670041061L;

	public static final String KEY_RESULT = "result";
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";

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
        Game tempGame = null;
        boolean missingParameter = false;
        String result = null;
        try {
        	tempGame = new Game(request);
        } catch (Exception e) {
        	missingParameter = true;
        	result = RESULT_PARAMETER_MISSING;
        }
        Game game = null;
        if (!missingParameter) {
	        String validity = tempGame.checkValidity();
	        if (validity.equals(Game.VALID)) {
	        	if (DB.doesGameExist(tempGame.getGameID())) result = Game.RESULT_GAME_EXISTS;
	        	else {
	        		game = DB.createGame(tempGame);
	        		if (game != null) {
	        			result = Game.RESULT_GAME_CREATED;
	        			jsonResponse.put(Game.KEY_GAME, game.toJSONString());
	        		}
	        		else result = Game.RESULT_OTHER_ERROR;
	        	}
	        }
	        else {
	        	result = validity;
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
