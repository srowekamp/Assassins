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
		String gameID, password, playerList, playersAlive, endTime;
		int id;
		float xPosition, yPosition;
		
		
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
    	
    }

}
