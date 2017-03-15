package gh.servlet.auth;

import gh.db.managers.AccountManager;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GoogleSign
 */
@WebServlet("/GoogleSign")
public class GoogleSign extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final String PROJECT_ID = "fresh-strategy-584";
	@SuppressWarnings("unused")
	private static final String PROJECT_NUM = "290990354966";
	@SuppressWarnings("unused")
	private static final String CLIENT_ID = "290990354966-7poh9j7rpsph7jclk4fjfspsf31gcfu3.apps.googleusercontent.com";
	@SuppressWarnings("unused")
	private static final String CLIENT_SECRET = "4CGvgj6-G3KLiYxLsm3PLDVa";
	@SuppressWarnings("unused")
	private static final String REDIRECT_URL = "http://localhost:8080/GameHours/GoogleSign";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GoogleSign() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}

	/**
	 * Attempts authentication via Google+.
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		// setting unicode encoding for inputs
		res.setContentType("UTF-8");
		res.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");

		String googleId = req.getParameter("username");
		String email = req.getParameter("email");
		String nickname = req.getParameter("nickname");
		String firstName = req.getParameter("firstname");
		String lastName = req.getParameter("lastname");
		String gender = req.getParameter("gender");
		String defGPassword = googleId + email + "salt";

		// handling either signing in or signing up
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		if (manager.usernameExists(googleId)) {
			// already registered, so just redirecting
			req.getSession().setAttribute("username", googleId);
			res.sendRedirect("index.jsp");
		} else {
			// registering account
			req.setAttribute("socialSign", "true");
			req.setAttribute("username", googleId);
			req.setAttribute("password", defGPassword);
			req.setAttribute("email", email);
			req.setAttribute("nickname", nickname);
			req.setAttribute("firstname", firstName);
			req.setAttribute("lastname", lastName);
			req.setAttribute("gender", gender);

			RequestDispatcher dispatcher = req.getRequestDispatcher("SignUp");
			dispatcher.forward(req, res);
		}
	}

}
