<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.util.List"%>
<%@page import="gh.datamodel.Review"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.db.managers.GameCatalog"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/reviews.css" rel="stylesheet">
<%
	GameCatalog catalog = (GameCatalog) getServletContext()
			.getAttribute(GameCatalog.ATTRIBUTE_NAME);
	String game_id_str = request.getParameter("game");
	if (game_id_str == null) {
		response.sendRedirect("index.jsp");
		return;
	}
	Integer gameId = Integer.parseInt(game_id_str);
	Game game = catalog.getGame(gameId);
	if (game == null) {
		response.sendRedirect("index.jsp");
		return;
	}
%>
<title><%=game.getName()%> Reviews</title>
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
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		acc = manager.getAccount(loggedInUser);
	%>
	<div class="jumbotron">
		<div class="container">
			<div class="col-md-3">
				<div class="row">
					<%
						String imageFile = "default.png";
						if (game.getImageName() != null)
							imageFile = game.getImageName();
					%>
					<a href="game.jsp?game=<%=game_id_str%>"><img
						class="img-rounded center-block bigthumb"
						src="GetImage?image=game_covers/standard/<%=imageFile%>"></a>
				</div>
				<div class="row text-center withmargins">
					<%
						if (acc != null && !manager.isBanned(acc)) {
							if (catalog.getReview(acc, game) == null) {
								out.print("<a class=\"btn btn-warning text-center\" ");
								out.print("href=\"addreview.jsp?game=");
								out.print(game_id_str);
								out.print("&author=");
								out.print(loggedInUser);
								out.print("\">Add Review</a>");
							} else {
								out.print("<a class=\"btn btn-warning text-center\" ");
								out.print("href=\"review.jsp?game=");
								out.print(game_id_str);
								out.print("&author=");
								out.print(loggedInUser);
								out.print("\">View My Review</a>");
							}
						}
					%>
				</div>
			</div>
			<div class="col-md-9">
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
					List<Review> reviews = catalog.getReviews(game, pageNum, limit);
					for (int i = 0; i < reviews.size(); ++i) {
						Review curr = reviews.get(i);
						String author = curr.getAuthorUsername();
						int rating = curr.getGivenRating();
						Account currAcc = manager.getAccount(author);
						String avatarName = currAcc.getAvatarFilename();
						String classByRating = "list-group-item";

						if (rating > 85)
							classByRating += " list-group-item-success";
						else if (rating > 60)
							classByRating += " list-group-item-info";
						else if (rating > 50)
							classByRating += " list-group-item-warning";
						else
							classByRating += " list-group-item-danger";

						String name = currAcc.getUsername();
						if (currAcc.getNickname() != null)
							name = currAcc.getNickname();

						if (acc != null) {
							out.print("<div class=\"list-group\">");
							out.print("<a href=\"profile.jsp?username="
									+ currAcc.getUsername() + "\" class=\""
									+ classByRating + "\">");
							if (avatarName == null)
								out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/default.jpg\">");
							else
								out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/"
										+ avatarName + "\">");
							out.print("<span class=\"glyphicon glyphicon-user\"></span>");
							out.print(name + "</a>");
							out.print("<span class=\"" + classByRating + "\">");
							out.print("<span class=\"glyphicon glyphicon-stats\"></span>");
							out.print(" Rating: " + rating + "%</span>");
							out.print("<a href=\"review.jsp?author=" + author
									+ "&game=" + game_id_str + "\" class=\""
									+ classByRating + "\">");
							out.print("<span class=\"glyphicon glyphicon-pencil\"></span>");
							out.print(" " + curr.getReviewText() + "</a>");
							out.print("</div>");
						} else {
							out.print("<ul class=\"list-group\">");
							out.print("<li class=\"" + classByRating + "\">");
							if (avatarName == null)
								out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/default.jpg\">");
							else
								out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/"
										+ avatarName + "\">");
							out.print("<span class=\"glyphicon glyphicon-user\"></span>");
							out.print(name + "</li>");
							out.print("<span class=\"" + classByRating + "\">");
							out.print("<span class=\"glyphicon glyphicon-stats\"></span>");
							out.print(" Rating: " + rating + "%</span>");
							out.print("<li class=\"" + classByRating + "\">");
							out.print("<span class=\"glyphicon glyphicon-pencil\"></span>");
							out.print(" " + curr.getReviewText() + "</li>");
							out.print("</ul>");
						}
					}
					out.print("<ul class=\"pager\">");
					int reviewsSoFar = (pageNum - 1) * limit + reviews.size();
					int reviewsNum = catalog.getReviewsQuantity(game);
					if (reviewsNum > reviews.size()) {
						if (pageNum == 1) {
							out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
						} else {
							out.print("<li><a href=\"gamereviews.jsp?game="
									+ game_id_str + "&page=");
							out.print(pageNum - 1);
							out.print("\">Previous</a></li>");
						}
						if (reviewsSoFar <= limit) {
							out.print("<li><a href=\"gamereviews.jsp?game="
									+ game_id_str + "&page=");
							out.print(pageNum + 1);
							out.print("\">Next</a></li>");
						} else {
							out.print("<li class=\"disabled\"><a href=\"gamereviews.jsp?game="
									+ game_id_str + "&page=");
							out.print(pageNum + 1);
							out.print("\">Next</a></li>");
						}
					}
					out.print("</ul>");
					out.print("</div>");
				%>
			</div>
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