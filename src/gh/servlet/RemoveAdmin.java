package gh.servlet;

import gh.datamodel.Account;
import gh.db.managers.AccountManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RemoveAdmin
 */
@WebServlet("/RemoveAdmin")
public class RemoveAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RemoveAdmin() {
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
		// getting account manager
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);

		String user = request.getParameter("user");
		if (user == null) {
			response.sendRedirect("index.jsp");
			return;
		}
		Account acc = manager.getAccount(user);
		manager.removeAdmin(acc);
		response.sendRedirect("profile.jsp?username=" + user);
	}

}
