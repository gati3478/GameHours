package gh.servlet;

import gh.datamodel.Account;
import gh.db.managers.AccountManager;
import gh.util.Hash;

import java.awt.image.BufferedImage;
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
 * Servlet implementation class AvatarUpload
 */
@WebServlet("/AvatarUpload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 50)
// 50MB
public class AvatarUpload extends DiskStorage {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_LOCATION_PROPERTY_KEY = "storage.location";
	private String uploadsDirName;
	private static final String SAVE_DIR = "avatars";
	private static final String STD_DIR = "standard_184x184";
	private static final String THUMB_DIR = "thumbnails_44x44";
	private static final int THUMB_WIDTH = 44;
	private static final int THUMB_HEIGHT = 44;
	private static final int STD_WIDTH = 184;
	private static final int STD_HEIGHT = 184;

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
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		String username = (String) request.getSession()
				.getAttribute("username");
		if (username == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		Account acc = manager.getAccount(username);
		// constructs path of the directory to save uploaded file
		String savePath = uploadsDirName + File.separator + SAVE_DIR;

		if (acc.getAvatarFilename() != null) {
			// deleting old one
			File fileStd = new File(savePath + File.separator + STD_DIR
					+ File.separator + acc.getAvatarFilename());
			fileStd.delete();
			File fileThumb = new File(savePath + File.separator + THUMB_DIR
					+ File.separator + acc.getAvatarFilename());
			fileThumb.delete();
		}

		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists())
			fileSaveDir.mkdir();

		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			if (!fileName.isEmpty()) {
				String extension = getFileExtension(fileName);
				try {
					fileName = Hash.hashText(username + "salt");
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
						STD_HEIGHT, Scalr.OP_ANTIALIAS);

				// resizing with our thumbnail avatar dimensions
				BufferedImage thumbImage = Scalr.resize(originalImage,
						THUMB_WIDTH, THUMB_HEIGHT, Scalr.OP_ANTIALIAS);
				ImageIO.write(originalImage, extension, new File(stdSavePath));
				ImageIO.write(thumbImage, extension, new File(thumbSavePath));

				// associates avatar with the account
				manager.changeAvatarName(acc, fileName + "." + extension);
			}
		}

		// redirecting to the previous page suer was browsing
		String toPage = request.getParameter("toPage");
		response.sendRedirect(toPage);
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
