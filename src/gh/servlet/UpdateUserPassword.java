package gh.servlet;

import gh.datamodel.Account;
import gh.db.managers.AccountManager;
import gh.util.Hash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpdateUserPassword
 */
@WebServlet("/UpdateUserPassword")
public class UpdateUserPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateUserPassword() {
		super();
		// TODO Auto-generated constructor stub
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

		String oldPassword = request.getParameter("old_password");
		String newPassword = request.getParameter("new_password");
		boolean canChange = false;

		// if user was registered via social services we allow to create new
		// password
		if (oldPassword == null) {
			canChange = true;
		} else {
			try {
				if (acc.getHashedPassword().equals(Hash.hashText(oldPassword)))
					canChange = true;
				else
					canChange = false;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// either changing password and redirecting to profile if successful, or
		// prompting user to enter old password again
		if (canChange) {
			try {
				manager.changeHashedPassword(acc, Hash.hashText(newPassword));
				response.sendRedirect("profile.jsp?username="
						+ acc.getUsername());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			request.setAttribute("error", "true");
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("changepassword.jsp");
			dispatcher.forward(request, response);
		}
	}

}
