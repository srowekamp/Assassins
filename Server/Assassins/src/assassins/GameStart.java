package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.Game;

public class GameStart extends HttpServlet {

	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 1788356290670041061L;

	public static final String KEY_RESULT = "result";
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_GAME_STARTED = "success";
	
	public static final String KEY_TARGET = "target";

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
        boolean missingParameter = false;
        String result = null;
        String gameID = request.getParameter(Game.KEY_GAMEID);
        String startTime = request.getParameter(Game.KEY_START_TIME);
        if (!Game.isValidGameID(gameID)) result = Game.RESULT_GAMEID_INVALID;
        else if (!Game.isValidStartTime(startTime)) result = Game.RESULT_START_TIME_INVALID;
        else {
        	Game game = DB.getGame(gameID);
        	game = DB.setEndTime(game, startTime);
        	game = DB.setTargetList(game);
        	UserAccount target = game.getTarget(game.getHostID());
        	jsonResponse.put(KEY_TARGET, target.toJSONString());
        	jsonResponse.put(Game.KEY_GAME, game);
        	result = RESULT_GAME_STARTED;
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
