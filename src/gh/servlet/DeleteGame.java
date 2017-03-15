package gh.servlet;

import gh.datamodel.Game;
import gh.db.managers.GameCatalog;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteGame
 */
@WebServlet("/DeleteGame")
public class DeleteGame extends DiskStorage {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_LOCATION_PROPERTY_KEY = "storage.location";
	private String uploadsDirName;
	private static final String COVER_DIR = "game_covers";
	private static final String IMAGE_DIR = "game_images";
	private static final String STD_DIR = "standard";
	private static final String THUMB_DIR = "thumbnail_120x120";

	@Override
	public void init() throws ServletException {
		super.init();
		uploadsDirName = property(UPLOAD_LOCATION_PROPERTY_KEY);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// getting game catalog
		GameCatalog catalog = (GameCatalog) getServletContext().getAttribute(
				GameCatalog.ATTRIBUTE_NAME);

		String game_str = request.getParameter("game_id");
		Game game = catalog.getGame(Integer.parseInt(game_str));

		// removing associated files
		String savePath = uploadsDirName + File.separator + COVER_DIR;
		String stdPath = savePath + File.separator + STD_DIR + File.separator
				+ game.getImageName();
		String thumbPath = savePath + File.separator + THUMB_DIR
				+ File.separator + game.getImageName();
		File stdFile = new File(stdPath);
		stdFile.delete();
		File thumbFile = new File(thumbPath);
		thumbFile.delete();

		List<String> imageFiles = catalog.getImages(game);
		String imagesPath = uploadsDirName + File.separator + IMAGE_DIR;
		for (int i = 0; i < imageFiles.size(); ++i) {
			String currImagePath = imagesPath + File.separator
					+ imageFiles.get(i);
			File fileToDelete = new File(currImagePath);
			fileToDelete.delete();
		}
		// removing from database
		catalog.removeGame(game);
		response.sendRedirect("gamecatalog.jsp?");
	}

}
