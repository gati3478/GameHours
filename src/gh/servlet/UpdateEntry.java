package gh.servlet;

import gh.datamodel.Entry;
import gh.datamodel.GameplayTimes;
import gh.db.managers.GameCatalog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpdateEntry
 */
@WebServlet("/UpdateEntry")
public class UpdateEntry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateEntry() {
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
		String entry_id = request.getParameter("entry_id");
		String game_id = request.getParameter("game_id");

		String username = (String) request.getSession()
				.getAttribute("username");
		if (username == null || entry_id == null || game_id == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		// parsing and checking data
		Integer mainHours = null, extraHours = null, completeHours = null;
		Integer entryId = Integer.parseInt(entry_id);
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
			Entry updatedEntry = new Entry(entryId, username, gameId, platform,
					times, null);
			catalog.updateEntry(updatedEntry);
			response.sendRedirect(request.getParameter("toPage"));
		} else {
			response.sendRedirect("editentry.jsp?id=" + entryId);
		}
	}
}
