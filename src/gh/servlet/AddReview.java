package gh.servlet;

import gh.datamodel.Review;
import gh.db.managers.GameCatalog;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddReview
 */
@WebServlet("/AddReview")
public class AddReview extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddReview() {
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
		String author = (String) request.getSession().getAttribute("username");
		String game_id = request.getParameter("game");
		String rating = request.getParameter("rating");
		String reviewText = request.getParameter("reviewText");

		// checking data and executing adding
		if (author != null && game_id != null) {
			Integer givenRating = Integer.parseInt(rating);
			Integer gameId = Integer.parseInt(game_id);
			// get current date time with Calendar()
			java.util.Date date = new java.util.Date();
			Timestamp currDate = new Timestamp(date.getTime());
			Review newReview = new Review(author, gameId, givenRating,
					reviewText, currDate);
			catalog.addReview(newReview);
			response.sendRedirect(request.getParameter("toPage"));
		} else {
			response.sendRedirect("addreview.jsp?game=" + game_id);
		}
	}

}
