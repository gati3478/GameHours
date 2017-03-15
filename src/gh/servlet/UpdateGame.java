package gh.servlet;

import gh.datamodel.Game;
import gh.db.managers.GameCatalog;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UpdateGame
 */
@WebServlet("/UpdateGame")
public class UpdateGame extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateGame() {
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
		Game game = catalog.getGame(Integer.parseInt(request
				.getParameter("gameid")));

		// some basic error checking
		if (request.getSession().getAttribute("username") == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		// getting data
		String gameName = request.getParameter("game");
		String developers = request.getParameter("developers");
		String publishers = request.getParameter("publishers");
		String[] platforms = request.getParameterValues("platformsList");
		String[] genres = request.getParameterValues("genresList");
		String releaseDate = request.getParameter("releaseDate");
		String shortDescription = request.getParameter("shortDescription");

		List<String> platformList = new ArrayList<String>();
		List<String> genreList = new ArrayList<String>();
		if (platforms != null) {
			for (String str : platforms)
				platformList.add(str);
		}
		if (genres != null) {
			for (String str : genres)
				genreList.add(str);
		}

		// changing database entries for game
		catalog.changeGameName(game, gameName);
		if ("".equals(developers))
			developers = null;
		catalog.changeGameDevelopers(game, developers);

		if ("".equals(publishers))
			publishers = null;
		catalog.changeGamePublishers(game, publishers);

		// removing old genres
		for (int i = 0; i < game.getGenres().size(); ++i)
			catalog.removeGameGenre(game, game.getGenres().get(i));
		// adding new genres
		for (int i = 0; i < genreList.size(); ++i)
			catalog.addGameGenre(game, genreList.get(i));

		// removing old platforms
		for (int i = 0; i < game.getPlatforms().size(); ++i)
			catalog.removeGamePlatform(game, game.getPlatforms().get(i));
		// adding new platforms
		for (int i = 0; i < platformList.size(); ++i)
			catalog.addGamePlatform(game, platformList.get(i));

		if ("".equals(releaseDate))
			releaseDate = null;
		if (releaseDate != null)
			catalog.changeGameReleaseDate(game, Date.valueOf(releaseDate));
		if ("".equals(shortDescription))
			shortDescription = null;
		catalog.changeGameShortDescription(game, shortDescription);
		response.sendRedirect("game.jsp?game=" + game.getID());

	}

}
