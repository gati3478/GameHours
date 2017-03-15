package gh.servlet;

import gh.datamodel.Review;
import gh.db.managers.GameCatalog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteReview
 */
@WebServlet("/DeleteReview")
public class DeleteReview extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteReview() {
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
		// getting game catalog
		GameCatalog catalog = (GameCatalog) getServletContext().getAttribute(
				GameCatalog.ATTRIBUTE_NAME);
		String author = request.getParameter("author");
		String game_id = request.getParameter("game");
		if (author == null || game_id == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		// deleting and redirecting
		Integer gameId = Integer.parseInt(game_id);
		Review review = new Review(author, gameId, null, null, null);
		catalog.removeReview(review);
		response.sendRedirect(request.getParameter("toPage"));
	}

}
