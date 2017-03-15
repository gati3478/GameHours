package gh.servlet.auth;

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
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SignIn
 */
@WebServlet("/SignIn")
public class SignIn extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignIn() {
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
		String username = (String) request.getParameter("username");
		if (username == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		String password = (String) request.getParameter("password");

		try {
			String hashedPassword = Hash.hashText(password);
			AccountManager manager = (AccountManager) getServletContext()
					.getAttribute(AccountManager.ATTRIBUTE_NAME);
			if (manager.usernameExists(username)) {
				if (manager.authenticateUser(username, hashedPassword)) {
					HttpSession session = request.getSession();
					session.setAttribute("username", username);
					response.sendRedirect("index.jsp");
				} else {
					request.setAttribute("incorrectPassword", "true");
					RequestDispatcher dispatcher = request
							.getRequestDispatcher("signin.jsp");
					dispatcher.forward(request, response);
				}
			} else {
				request.setAttribute("incorrectUsername", "true");
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("signin.jsp");
				dispatcher.forward(request, response);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
