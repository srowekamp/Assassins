package assassins;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import assassins.UserAccount;
import assassins.DB;

public class Kill extends HttpServlet {
	
	/**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 4322300821194882221L;
	public static final String KEY_RESULT = "result";
	
	public static final String RESULT_PARAMETER_MISSING = "parameter_error";
	public static final String RESULT_ERROR = "error"; // Result when there is an error. Shouldn't occur
	public static final String RESULT_SUCCESS = "success"; // Result when the kill is processed successfully
	
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
    	JSONObject jsonResponse = new JSONObject();
        String result = null;
        boolean parameterMissing = false;
        String gameID = null;
        int playerID = -1;
        try {
        	gameID = request.getParameter(Game.KEY_GAMEID);
        	playerID = Integer.parseInt(request.getParameter(UserAccount.KEY_ID));
        } catch (Exception e) {
        	result = RESULT_PARAMETER_MISSING;
        	parameterMissing = true;
        }
        if (!parameterMissing) { // TODO ensure values grabbed from request are valid
        	Game game = DB.getGame(gameID);
        	if (game == null) {
        		result = RESULT_ERROR;
        	}
        	else {
        		UserAccount target = game.getTarget(playerID);
        		game = game.killPlayer(target.getUserID());
        		if (game == null) {
        			result = RESULT_ERROR;
        		}
        		else {
        			UserAccount player = DB.addKill(playerID);
        			if (player == null) {
        				result = RESULT_ERROR;
        			} else {
        				result = RESULT_SUCCESS;
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