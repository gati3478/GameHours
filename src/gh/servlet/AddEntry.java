package gh.servlet;

import gh.datamodel.Entry;
import gh.datamodel.GameplayTimes;
import gh.db.managers.GameCatalog;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddEntry
 */
@WebServlet("/AddEntry")
public class AddEntry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddEntry() {
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

		// getting game catalog
		GameCatalog catalog = (GameCatalog) getServletContext().getAttribute(
				GameCatalog.ATTRIBUTE_NAME);

		// entry fields
		String main = request.getParameter("main");
		String extra = request.getParameter("extra");
		String complete = request.getParameter("complete");
		String platform = request.getParameter("platform");
		String game_id = request.getParameter("game_id");

		String username = (String) request.getSession()
				.getAttribute("username");
		if (username == null || game_id == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		// parsing and checking data
		Integer mainHours = null, extraHours = null, completeHours = null;
		Integer gameId = Integer.parseInt(game_id);
		if (main != null && !main.equals("") && !main.equals("0"))
			mainHours = Integer.parseInt(main);
		if (extra != null && !extra.equals("") && !extra.equals("0"))
			extraHours = Integer.parseInt(extra);
		if (complete != null && !complete.equals("") && !complete.equals("0"))
			completeHours = Integer.parseInt(complete);

		if (mainHours != null || extraHours != null || completeHours != null) {
			GameplayTimes times = new GameplayTimes(mainHours, extraHours,
					completeHours);
			// get current date time with Calendar()
			java.util.Date date= new java.util.Date();
			Timestamp currDate = new Timestamp(date.getTime());
			Entry newEntry = new Entry(username, gameId, platform, times,
					currDate);
			catalog.addEntry(newEntry);
			response.sendRedirect(request.getParameter("toPage"));
		} else {
			response.sendRedirect("addentry.jsp?game=" + gameId);
		}
	}

}
