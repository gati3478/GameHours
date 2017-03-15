package gh.servlet.auth;

import gh.datamodel.Account;
import gh.datamodel.Account.AccountBuilder;
import gh.db.managers.AccountManager;
import gh.util.Hash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SignUp
 */
@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignUp() {
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

		// getting username either from social login or the web-page form
		String username = null;
		if (request.getAttribute("socialSign") == null)
			username = request.getParameter("username");
		else
			username = (String) request.getAttribute("username");

		// handling account creation
		if (!manager.usernameExists(username)) {
			Account acc = buildAccount(username, request);
			if (manager.createAccount(acc)) {
				request.getSession()
						.setAttribute("username", acc.getUsername());
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("setavatar.jsp");
				dispatcher.forward(request, response);
			} else {
				request.setAttribute("error", "true");
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("signup.jsp");
				dispatcher.forward(request, response);
			}
		} else {
			request.setAttribute("usernameTaken", "true");
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("signup.jsp");
			dispatcher.forward(request, response);
		}
	}

	/*
	 * Builds and returns Account object based on values passed to the request
	 * object.
	 */
	private Account buildAccount(String username, HttpServletRequest request) {
		String password, emailAddr;
		String nickname, firstName, lastName;
		String birthdate, gender, country;
		String steam_id, psn_id, xbox_id;

		// checking if values were passed either from the request dispatcher or
		// the web-page form
		if (request.getAttribute("socialSign") != null) {
			password = (String) request.getAttribute("password");
			emailAddr = (String) request.getAttribute("email");
			nickname = (String) request.getAttribute("nickname");
			firstName = (String) request.getAttribute("firstname");
			lastName = (String) request.getAttribute("lastname");
			birthdate = (String) request.getAttribute("birthdate");
			gender = (String) request.getAttribute("gender");
			country = (String) request.getAttribute("country");
			steam_id = (String) request.getAttribute("steam_id");
			psn_id = (String) request.getAttribute("psn_id");
			xbox_id = (String) request.getAttribute("xbox_id");
		} else {
			password = request.getParameter("password");
			emailAddr = request.getParameter("email");
			nickname = request.getParameter("nickname");
			firstName = request.getParameter("firstname");
			lastName = request.getParameter("lastname");
			birthdate = request.getParameter("birthdate");
			gender = request.getParameter("gender");
			country = request.getParameter("country");
			steam_id = request.getParameter("steam_id");
			psn_id = request.getParameter("psn_id");
			xbox_id = request.getParameter("xbox_id");
		}

		// hashing password
		String hashedPassword = null;
		try {
			hashedPassword = Hash.hashText(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// building account
		AccountBuilder builder = new Account.AccountBuilder(username,
				hashedPassword, emailAddr);

		if (nickname != null && !nickname.equals(""))
			builder.nickname(nickname);
		if (firstName != null && !firstName.equals(""))
			builder.firstName(firstName);
		if (lastName != null && !lastName.equals(""))
			builder.lastName(lastName);
		if (birthdate != null && !birthdate.equals(""))
			builder.birthdate(Date.valueOf(birthdate));
		if (gender != null && !gender.equals(""))
			builder.gender(gender.toLowerCase());
		if (country != null && !country.equals(""))
			builder.country(country);
		if (steam_id != null && !steam_id.equals(""))
			builder.steamID(steam_id);
		if (psn_id != null && !psn_id.equals(""))
			builder.playStationNetworkID(psn_id);
		if (xbox_id != null && !xbox_id.equals(""))
			builder.xboxLiveGamertag(xbox_id);
		return builder.build();
	}

}
