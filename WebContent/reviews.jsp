<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.util.List"%>
<%@page import="gh.datamodel.Review"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="java.sql.Timestamp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/admin.css" rel="stylesheet">
<title>All Reviews</title>
</head>
<body>
	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.jsp"><img id="navbarlogo"
					src="icons/gh_icon_42px.png"></a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a href="index.jsp">MAIN</a></li>
					<%
						String loggedInUser = (String) session.getAttribute("username");
						if (loggedInUser != null) {
							out.print("<li><a href=\"profile.jsp?username=");
							out.print(loggedInUser);
							out.print("\">PROFILE</a></li>");
						}
					%>
					<li><a href="gamecatalog.jsp">GAME CATALOG</a></li>
					<li><a href="stats.jsp">STATS</a></li>
					<li><a href="about.jsp">ABOUT</a></li>
				</ul>
				<%@include file="sign.jsp"%>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<%
		Account acc = null;
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
		if (loggedInUser == null) {
			response.sendRedirect("index.jsp");
			return;
		} else {
			AccountManager manager = (AccountManager) getServletContext()
					.getAttribute(AccountManager.ATTRIBUTE_NAME);
			acc = manager.getAccount(loggedInUser);
			if (!manager.isAdmin(acc)) {
				response.sendRedirect("index.jsp");
				return;
			}
		}
	%>
	<div class="jumbotron">
		<div class="container">
			<div class="alert alert-info text-center withmargins">Game
				Reviews</div>
			<%
				String pageNumStr = request.getParameter("page");
				int pageNum;
				int limit = 13;
				if (pageNumStr != null)
					pageNum = Integer.parseInt(pageNumStr);
				else
					pageNum = 1;
				List<Review> reviews = catalog.getReviews(pageNum, limit);
				for (int i = 0; i < reviews.size(); ++i) {
					Review curr = reviews.get(i);
					String author = curr.getAuthorUsername();
					int game_id = curr.getGameID();
					int rating = curr.getGivenRating();
					Timestamp revDate = curr.getReviewDate();
					Game game = catalog.getGame(game_id);

					if (rating >= 85)
						out.print("<div class=\"alert alert-success withmargins\">");
					else if (rating >= 60)
						out.print("<div class=\"alert alert-info withmargins\">");
					else if (rating >= 50)
						out.print("<div class=\"alert alert-warning withmargins\">");
					else
						out.print("<div class=\"alert alert-danger withmargins\">");
					out.print("<a href=\"review.jsp?author=" + author + "&game="
							+ game_id + "\" class=\"alert-link\">");
					out.print("<span class=\"glyphicon glyphicon-pencil\"></span>");
					out.print(" " + game.getName() + ";");
					out.print(" Author: " + curr.getAuthorUsername() + ";");
					out.print(" RATING: <span class=\"label label-primary\">" + rating + "%</span>;");
					out.print(" SUBMITTED: " + revDate + ";");
					out.print("</a>");
					out.print("</div>");
				}
				out.print("<ul class=\"pager\">");
				int reviewsSoFar = (pageNum - 1) * limit + reviews.size();
				int reviewsNum = catalog.getReviewsQuantity();
				if (reviewsNum > reviews.size()) {
					if (pageNum == 1) {
						out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
					} else {
						out.print("<li><a href=\"reviews.jsp?page=");
						out.print(pageNum - 1);
						out.print("\">Previous</a></li>");
					}
					if (reviewsSoFar <= limit) {
						out.print("<li><a href=\"reviews.jsp?page=");
						out.print(pageNum + 1);
						out.print("\">Next</a></li>");
					} else {
						out.print("<li class=\"disabled\"><a href=\"reviews.jsp?page=");
						out.print(pageNum + 1);
						out.print("\">Next</a></li>");
					}
				}
				out.print("</ul>");
				out.print("</div>");
			%>
		</div>
	</div>
	<div id="footer">
		<div class="container">
			<p class="text-muted"><%@include file="html/footer.html"%>
			</p>
		</div>
	</div>
	<!-- /container -->
	<%@include file="html/include_scripts.html"%>
</body>
</html>