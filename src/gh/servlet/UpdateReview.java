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
 * Servlet implementation class UpdateReview
 */
@WebServlet("/UpdateReview")
public class UpdateReview extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateReview() {
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

		// review fields
		String author = request.getParameter("author");
		String game_id = request.getParameter("game");
		String rating = request.getParameter("rating");
		String reviewText = request.getParameter("reviewText");

		// checking data and executing update
		if (author != null && game_id != null) {
			Integer givenRating = Integer.parseInt(rating);
			Integer gameId = Integer.parseInt(game_id);
			Review updatedReview = new Review(author, gameId, givenRating,
					reviewText, null);
			catalog.updateReview(updatedReview);
			response.sendRedirect(request.getParameter("toPage"));
		} else {
			response.sendRedirect("editreview.jsp?author=" + author + "&game="
					+ game_id);
		}
	}

}
