package assassins;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageUploadTest extends HttpServlet {
	
    /**
	 * Auto-generated number
	 */
	private static final long serialVersionUID = 5463499288825873708L;
	
	public static final String KEY_IMAGE = "image";
    public static final String KEY_NAME = "name";

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
    	String name = request.getParameter(KEY_NAME);
    	String b64image = request.getParameter(KEY_IMAGE);
    	String result = "fail";
    	if (name != null && b64image != null && name.length() > 4 && b64image.length() > 4){
    		saveB64Image(name, b64image);
    		result = "success";
    	}
    		
    	
    	response.getWriter().write(result);
    }
    
    private boolean saveB64Image(String filename, String b64Image) {
    	String filepath = "/var/lib/tomcat/webapps/imageTest/";
    	byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(b64Image);
    	try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File outputImage = new File(filepath + filename);
	    	ImageIO.write(img, "jpg", outputImage);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    	return true;
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