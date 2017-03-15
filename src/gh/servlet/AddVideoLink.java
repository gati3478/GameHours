package gh.servlet;

import gh.datamodel.Game;
import gh.db.managers.GameCatalog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddVideoLink
 */
@WebServlet("/AddVideoLink")
public class AddVideoLink extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddVideoLink() {
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

		// getting data
		String game_id = request.getParameter("game");
		String videoLink = request.getParameter("link");
		videoLink = applyCorrections(videoLink);
		Game game = catalog.getGame(Integer.parseInt(game_id));
		if (catalog.addVideoLink(game, videoLink))
			response.sendRedirect("videos.jsp?game=" + game_id);
		else
			response.sendRedirect("index.jsp");
	}

	/*
	 * Corrects YouTube link if it's not embedded type.
	 */
	private String applyCorrections(String videoLink) {
		String result = videoLink;
		int pos = videoLink.indexOf("watch?v=");
		if (pos != -1)
			result = result.replace("watch?v=", "embed/");
		return result;
	}

}
