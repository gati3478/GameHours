package gh.servlet;

import gh.datamodel.Game;
import gh.db.managers.GameCatalog;
import gh.util.Hash;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class ImageUpload
 */
@WebServlet("/ImageUpload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50)
public class ImageUpload extends DiskStorage {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_LOCATION_PROPERTY_KEY = "storage.location";
	private String uploadsDirName;
	private static final String SAVE_DIR = "game_images";

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
		// setting unicode encoding for inputs
		response.setContentType("UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");

		// getting game catalog
		GameCatalog catalog = (GameCatalog) getServletContext().getAttribute(
				GameCatalog.ATTRIBUTE_NAME);

		// getting data
		String gameName = request.getParameter("game");

		// some basic error checking
		if (request.getSession().getAttribute("username") == null
				|| gameName == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		Game game = catalog.getGame(Integer.parseInt(gameName));
		String filename = uploadImage(request, response, catalog, game);
		if (catalog.addImage(game, filename))
			response.sendRedirect(request.getParameter("toPage"));
		else
			response.sendRedirect("addimage.jsp?game=" + game.getID());
	}

	/*
	 * Saves image to disk.
	 */
	private String uploadImage(HttpServletRequest request,
			HttpServletResponse response, GameCatalog catalog, Game game)
			throws IOException, IllegalStateException,
			IllegalArgumentException, ImagingOpException, ServletException {
		// constructs path of the directory to save uploaded file
		String savePath = uploadsDirName + File.separator + SAVE_DIR;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists())
			fileSaveDir.mkdir();

		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			int newFileIndex = catalog.getImagesQuantity(game) + 1;
			if (!fileName.isEmpty()) {
				String extension = getFileExtension(fileName);
				try {
					fileName = Hash.hashText(request.getParameter("game")
							+ newFileIndex + "salt");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// writing actual file
				String stdSavePath = savePath + File.separator + fileName + "."
						+ extension;
				part.write(stdSavePath);
				return fileName + "." + extension;
			}
		}
		return null;
	}

	/*
	 * Extracts file's extensions from given filename.
	 */
	private String getFileExtension(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0)
			extension = fileName.substring(i + 1);
		return extension;
	}

	/*
	 * Extracts file name from HTTP header content-disposition
	 */
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}

}
