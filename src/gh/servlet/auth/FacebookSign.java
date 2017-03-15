package gh.servlet.auth;

import gh.db.managers.AccountManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class FacebookSign
 */
@WebServlet("/FacebookSign")
public class FacebookSign extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String APP_ID = "323918654424794";
	private static final String APP_SECRET = "860784b983b6106f5573e5bf0abd2e59";
	private static final String REDIRECT_URL = "http://localhost:8080/GameHours/FacebookSign";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FacebookSign() {
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
	 * Attempts authentication via Facebook.
	 */
	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// setting utf-8 encoding for inputs
		res.setContentType("UTF-8");
		res.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		
		// error checking
		String code = req.getParameter("code");
		if (code == null || code.equals("")) {
			req.setAttribute("error", "true");
			RequestDispatcher dispatcher = req
					.getRequestDispatcher("signup.jsp");
			dispatcher.forward(req, res);
		}

		// building token url
		String token = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("https://graph.facebook.com/oauth/access_token?client_id=");
			builder.append(APP_ID);
			builder.append("&redirect_uri="
					+ URLEncoder.encode(REDIRECT_URL, "UTF-8"));
			builder.append("&client_secret=");
			builder.append(APP_SECRET);
			builder.append("&code=" + code);
			URL u = new URL(builder.toString());
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			token = b.toString();
			if (token.startsWith("{"))
				throw new Exception("Error on requesting token: " + token
						+ " with code: " + code);
		} catch (Exception e) {
			// an error occurred, handle this
		}

		String graph = null;
		try {
			String g = "https://graph.facebook.com/me?" + token;
			URL u = new URL(g);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			graph = b.toString();
		} catch (Exception e) {
			// an error occurred, handle this
		}

		// parsing user data from JSON
		String facebookId = null; // username
		String email = null;
		String firstName = null;
		String lastName = null;
		String nickname = null;
		String gender = null;
		try {
			JSONObject json = new JSONObject(graph);
			facebookId = json.getString("id");
			firstName = json.getString("first_name");
			lastName = json.getString("last_name");
			nickname = firstName + " " + lastName;
			if (json.has("email"))
				email = json.getString("email");
			else
				email = "" + facebookId +"@facebook.com";
			if (json.has("gender"))
				gender = json.getString("gender");
		} catch (JSONException e) {
			// an error occurred, handle this
		}

		String defFbPassword = facebookId + email + "salt";

		// handling either signing in or signing up
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		if (manager.usernameExists(facebookId)) {
			// already registered, so just redirecting
			req.getSession().setAttribute("username", facebookId);
			res.sendRedirect("index.jsp");
		} else {
			// registering account
			req.setAttribute("socialSign", "true");
			req.setAttribute("username", facebookId);
			req.setAttribute("password", defFbPassword);
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
