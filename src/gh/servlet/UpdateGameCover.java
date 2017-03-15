package gh.servlet;

import gh.datamodel.Game;
import gh.db.managers.GameCatalog;
import gh.util.Hash;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.imgscalr.Scalr;

/**
 * Servlet implementation class UpdateGameCover
 */
@WebServlet("/UpdateGameCover")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50)
public class UpdateGameCover extends DiskStorage {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_LOCATION_PROPERTY_KEY = "storage.location";
	private String uploadsDirName;
	private static final String SAVE_DIR = "game_covers";
	private static final String STD_DIR = "standard";
	private static final String THUMB_DIR = "thumbnail_120x120";
	private static final int STD_WIDTH = 300;
	private static final int THUMB_WIDTH = 120;
	@SuppressWarnings("unused")
	private static final int THUMB_HEIGHT = 120;

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

		// some basic error checking
		String game_id = request.getParameter("game");
		if (game_id == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		// writing to disk and updating in database
		Game game = catalog.getGame(Integer.parseInt(game_id));
		String filename = uploadImage(request, response, game);
		if (catalog.changeGameImage(game, filename))
			response.sendRedirect("game.jsp?game=" + game_id);
		else
			response.sendRedirect("setcoverimage.jsp?game=" + game_id);
	}

	/*
	 * Saves image to disk.
	 */
	private String uploadImage(HttpServletRequest request,
			HttpServletResponse response, Game game) throws IOException,
			IllegalStateException, IllegalArgumentException,
			ImagingOpException, ServletException {
		// constructs path of the directory to save uploaded file
		String savePath = uploadsDirName + File.separator + SAVE_DIR;

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists())
			fileSaveDir.mkdir();

		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			if (!fileName.isEmpty()) {
				String extension = getFileExtension(fileName);
				try {
					fileName = Hash.hashText(game.getID() + "salt");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// deleting old one
				File fileStd = new File(savePath + File.separator + STD_DIR
						+ File.separator + game.getImageName());
				fileStd.delete();
				File fileThumb = new File(savePath + File.separator + THUMB_DIR
						+ File.separator + game.getImageName());
				fileThumb.delete();

				// writing actual file
				String stdSavePath = savePath + File.separator + STD_DIR
						+ File.separator + fileName + "." + extension;
				String thumbSavePath = savePath + File.separator + THUMB_DIR
						+ File.separator + fileName + "." + extension;
				part.write(stdSavePath);

				// loading uploaded avatar
				BufferedImage originalImage = ImageIO
						.read(new File(stdSavePath));

				// resizing with our standard avatar dimensions
				originalImage = Scalr.resize(originalImage, STD_WIDTH,
						Scalr.OP_ANTIALIAS);

				// resizing and cropping with our thumbnail avatar dimensions
				BufferedImage thumbImage = Scalr.resize(originalImage,
						THUMB_WIDTH, Scalr.OP_ANTIALIAS);
				if (thumbImage.getWidth() < thumbImage.getHeight())
					thumbImage = Scalr.crop(thumbImage, thumbImage.getWidth(),
							thumbImage.getWidth(), Scalr.OP_ANTIALIAS);
				ImageIO.write(originalImage, extension, new File(stdSavePath));
				ImageIO.write(thumbImage, extension, new File(thumbSavePath));

				// associates avatar with the account
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
