package gh.servlet;

import gh.datamodel.Account;
import gh.db.managers.AccountManager;
import gh.util.Hash;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteAccount
 */
@WebServlet("/DeleteAccount")
public class DeleteAccount extends DiskStorage {
	private static final long serialVersionUID = 1L;
	private static final String STORAGE_LOCATION_PROPERTY_KEY = "storage.location";
	private String uploadsDirName;
	private static final String SAVE_DIR = "avatars";
	private static final String STD_DIR = "standard_184x184";
	private static final String THUMB_DIR = "thumbnails_44x44";

	@Override
	public void init() throws ServletException {
		super.init();
		uploadsDirName = property(STORAGE_LOCATION_PROPERTY_KEY);
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

		// getting account manager
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);

		String username = (String) request.getSession()
				.getAttribute("username");
		if (username == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		Account acc = manager.getAccount(username);

		String password = request.getParameter("password");
		String hashedPassword = null;
		// hashing depends on whether account is social or not
		try {
			if (password != null)
				hashedPassword = Hash.hashText(password);
			else
				hashedPassword = Hash.hashText(acc.getUsername()
						+ acc.getEmail() + "salt");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (acc.getHashedPassword().equals(hashedPassword)) {
			manager.removeAccount(acc);
			request.getSession().setAttribute("username", null);
			// deleting avatar files
			String savePath = uploadsDirName + File.separator + SAVE_DIR;
			String stdPath = savePath + File.separator + STD_DIR
					+ File.separator + acc.getAvatarFilename();
			String thumbPath = savePath + File.separator + THUMB_DIR
					+ File.separator + acc.getAvatarFilename();
			File stdFile = new File(stdPath);
			stdFile.delete();
			File thumbFile = new File(thumbPath);
			thumbFile.delete();
			// redirecting
			response.sendRedirect("index.jsp");
		} else {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("deleteaccount.jsp");
			dispatcher.forward(request, response);
		}
	}

}
