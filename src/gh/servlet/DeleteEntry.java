package gh.servlet;

import gh.datamodel.Entry;
import gh.db.managers.GameCatalog;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteEntry
 */
@WebServlet("/DeleteEntry")
public class DeleteEntry extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteEntry() {
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
		String entry_id = request.getParameter("entry_id");
		if (entry_id == null) {
			response.sendRedirect("index.jsp");
			return;
		}

		Integer entryId = Integer.parseInt(entry_id);
		try {
			Entry entry = catalog.getEntry(entryId);
			catalog.removeEntry(entry);
			response.sendRedirect(request.getParameter("toPage"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
