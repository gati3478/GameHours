package gh.servlet;

import gh.datamodel.Account;
import gh.db.managers.AccountManager;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpdateUserInfo
 */
@WebServlet("/UpdateUserInfo")
public class UpdateUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateUserInfo() {
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

		// grabbing info
		String emailAddr = request.getParameter("email");
		String nickname = request.getParameter("nickname");
		String firstName = request.getParameter("firstname");
		String lastName = request.getParameter("lastname");
		String birthdate = request.getParameter("birthdate");
		String gender = request.getParameter("gender");
		String country = request.getParameter("country");
		String steam_id = request.getParameter("steam_id");
		String psn_id = request.getParameter("psn_id");
		String xbox_id = request.getParameter("xbox_id");

		// updating email address, assuming it is mandatory field
		manager.changeEmail(acc, emailAddr);

		// if other fields were left blank, set them null
		if ("".equals(nickname))
			nickname = null;
		manager.changeNickname(acc, nickname);

		if ("".equals(firstName))
			firstName = null;
		manager.changeFirstName(acc, firstName);

		if ("".equals(lastName))
			lastName = null;
		manager.changeLastName(acc, lastName);

		if ("".equals(birthdate))
			birthdate = null;
		if (birthdate != null)
			manager.changeBirthdate(acc, Date.valueOf(birthdate));
		else
			manager.changeBirthdate(acc, null);

		if ("".equals(gender))
			gender = null;
		if (gender != null)
			manager.changeGender(acc, gender.toLowerCase());
		else
			manager.changeGender(acc, gender);

		if ("".equals(country))
			country = null;
		manager.changeCountry(acc, country);

		if ("".equals(steam_id))
			steam_id = null;
		manager.changeSteamID(acc, steam_id);

		if ("".equals(psn_id))
			psn_id = null;
		manager.changePlayStationNetworkID(acc, psn_id);

		if ("".equals(xbox_id))
			xbox_id = null;
		manager.changeXboxLiveGamertag(acc, xbox_id);

		// redirecting to user profile
		response.sendRedirect("profile.jsp?username=" + acc.getUsername());
	}

}
