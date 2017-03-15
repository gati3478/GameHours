<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="gh.db.managers.Statistics"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/background.css" rel="stylesheet">
<title>Game Catalog</title>
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
					<li class="active"><a href="gamecatalog.jsp">GAME CATALOG</a></li>
					<li><a href="stats.jsp">STATS</a></li>
					<li><a href="about.jsp">ABOUT</a></li>
				</ul>
				<%@include file="sign.jsp"%>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<%
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
		Statistics stat = (Statistics) getServletContext().getAttribute(
				Statistics.ATTRIBUTE_NAME);
	%>

	<div class="jumbotron">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-1"></div>
				<div class="col-md-8">
					<%
						boolean isSearch = false;
						List<Game> games = null;
						String pageNumStr = request.getParameter("page");
						int pageNum;
						int limit = 8;
						if (pageNumStr != null)
							pageNum = Integer.parseInt(pageNumStr);
						else
							pageNum = 1;

						String enteredGameName = request.getParameter("game_name");
						String enteredGenre = request.getParameter("genresList");
						String enteredMinT = request.getParameter("min_main");
						String enteredMaxT = request.getParameter("max_main");
						Integer startTime = null;
						Integer endTime = null;
						if ((enteredGameName == null && enteredGenre == null
								&& enteredMinT == null && enteredMaxT == null)
								|| ("".equals(enteredGameName) && "".equals(enteredGenre)
										&& "".equals(enteredMinT) && "".equals(enteredMaxT))) {
							games = stat.getPopularGames(pageNum, limit);
						} else {
							isSearch = true;
							if ("".equals(enteredGameName)) {
								enteredGameName = null;
							}
							if ("".equals(enteredGenre)) {
								enteredGenre = null;
							}
							if (!"".equals(enteredMinT)) {
								startTime = Integer.parseInt(enteredMinT);
							}
							if (!"".equals(enteredMaxT)) {
								endTime = Integer.parseInt(enteredMaxT);
							}
							games = catalog.searchGame(enteredGameName, enteredGenre,
									startTime, endTime, pageNum, limit);
						}

						if (games.size() == 0) {
							out.print("<h1 class=\"text-center\">");
							out.print("<span class=\"label label-warning\">");
							out.print("No More Games :(");
							out.print("</span>");
							out.print("</h1>");
						}

						for (int i = 0; i < games.size(); ++i) {
							if (i % 2 == 0) {
								out.print("<div class=\"row\">");
							}
							out.print("<div class=\"col-md-6\">");
							out.print("<div class=\"row-center\">");
							Game game = games.get(i);
							String gameName = game.getName();
							out.print("<a href=\"game.jsp?game=" + game.getID() + "\">");
							out.print("<h4><span class=\"label label-success\">" + gameName
									+ "" + "</span></h4></a>");
							out.print("</div>");
							out.print("<div class=\"col-md-4\">");
							String gameCover = games.get(i).getImageName();
							out.print("<a href=\"game.jsp?game=" + game.getID() + "\">");
							out.print("<img class=\"img img-rounded block-center\" src=\"GetImage?image=game_covers/thumbnail_120x120/"
									+ gameCover + "\"></a>");
							out.print("</div>");
							out.print("<div class=\"col-md-8\">");
							out.print("<ul class=\"list-group\">");
							Integer main = stat.getAverageGameplayTimes(game)
									.getMainGameplayTime();
							Integer extra = stat.getAverageGameplayTimes(game)
									.getExtraGameplayTime();
							Integer complete = stat.getAverageGameplayTimes(game)
									.getCompleteGameplayTime();
							out.print("<li class=\"list-group-item list-group-item-success\">");
							out.print("<span class=\"badge\">" + main + "" + "</span>");
							out.print("MAIN HOURS");
							out.print("</li>");
							out.print("<li class=\"list-group-item list-group-item-info\">");
							out.print("<span class=\"badge\">" + extra + "" + "</span>");
							out.print("EXTRA HOURS");
							out.print("</li>");
							out.print("<li class=\"list-group-item list-group-item-danger\">");
							out.print("<span class=\"badge\">" + complete + "" + "</span>");
							out.print("COMPLETE HOURS");
							out.print("</li>");
							out.print("</ul>");
							out.print("</div>");
							out.print("</div>");
							if ((i + 1) < games.size()) {
								i++;
								out.print("<div class=\"col-md-6\">");
								out.print("<div class=\"row-center\">");
								Game game1 = games.get(i);
								String gameName1 = game1.getName();
								out.print("<a href=\"game.jsp?game=" + game1.getID()
										+ "\">");
								out.print("<h4><span class=\"label label-success\">"
										+ gameName1 + "" + "</span></h4></a>");
								out.print("</div>");
								out.print("<div class=\"col-md-4\">");
								String gameCover1 = game1.getImageName();
								out.print("<a href=\"game.jsp?game=" + game1.getID()
										+ "\">");
								out.print("<img class=\"img img-rounded block-center\" src=\"GetImage?image=game_covers/thumbnail_120x120/"
										+ gameCover1 + "\"></a>");
								out.print("</div>");
								out.print("<div class=\"col-md-8\">");
								out.print("<ul class=\"list-group\">");
								Integer main1 = stat.getAverageGameplayTimes(game1)
										.getMainGameplayTime();
								Integer extra1 = stat.getAverageGameplayTimes(game1)
										.getExtraGameplayTime();
								Integer complete1 = stat.getAverageGameplayTimes(game1)
										.getCompleteGameplayTime();
								out.print("<li class=\"list-group-item list-group-item-success\">");
								out.print("<span class=\"badge\">" + main1 + "" + "</span>");
								out.print("MAIN HOURS");
								out.print("</li>");
								out.print("<li class=\"list-group-item list-group-item-info\">");
								out.print("<span class=\"badge\">" + extra1 + ""
										+ "</span>");
								out.print("EXTRA HOURS");
								out.print("</li>");
								out.print("<li class=\"list-group-item list-group-item-danger\">");
								out.print("<span class=\"badge\">" + complete1 + ""
										+ "</span>");
								out.print("COMPLETE HOURS");
								out.print("</li>");
								out.print("</ul>");
								out.print("</div>");
								out.print("</div>");
								out.print("</div>");
							} else {
								out.print("</div>");
							}
						}
					%>

				</div>
				<div class="col-md-3">
					<form action="gamecatalog.jsp" method="get"
						class="form-horizontal form-search" id="gh_search" role="form"
						accept-charset="UTF-8">
						<h2 class="text-center">
							<span class="label label-info">Search</span>
						</h2>
						<div class="form-group">
							<label for="inputGamename" class="col-sm-4 control-label">Gamename:</label>
							<div class="col-sm-8">
								<input type="text" class="form-control" placeholder="Gamename"
									id="game_name" name="game_name">
							</div>
						</div>
						<div class="form-group">
							<label for="inputGender" class="col-sm-4 control-label">Genre:</label>
							<div class="col-sm-8">
								<%@include file="html/genresSearch.html"%>
							</div>
						</div>
						<div class="form-group">
							<label for="inputStartTime" class="col-sm-4 control-label">Min
								time:</label>
							<div class="col-sm-8">
								<input type="number" class="form-control" placeholder="Min time"
									id="min_main" name="min_main">
							</div>
						</div>
						<div class="form-group">
							<label for="inputStartTime" class="col-sm-4 control-label">Max
								time:</label>
							<div class="col-sm-8">
								<input type="number" class="form-control" placeholder="Max time"
									id="max_main" name="max_main">
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-offset-4 col-sm-8">
								<button class="btn btn-lg btn-danger btn-block" type="submit">Search</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">
				<%
					out.print("<ul class=\"pager\">");
					int gamesNum = catalog.getCatalogSize();
					String game_name = enteredGameName;
					String game_genre = enteredGenre;
					String minTime = enteredMinT;
					String maxTime = enteredMaxT;
					if (game_name == null) {
						game_name = "";
					}
					if (game_genre == null) {
						game_genre = "";
					}
					if (minTime == null) {
						minTime = "";
					}
					if (maxTime == null) {
						maxTime = "";
					}
					if (pageNum != 1) {
						out.print("<li><a href=\"gamecatalog.jsp?page=");
						out.print(pageNum - 1);
						if (isSearch) {
							out.print("&game_name=" + game_name);
							out.print("&genresList=" + game_genre);
							out.print("&min_main=" + minTime);
							out.print("&max_main=" + maxTime);
						}
						out.print("\">Previous</a></li>");
					} else {
						out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
					}
					if (games.size() == limit) {
						out.print("<li><a href=\"gamecatalog.jsp?page=");
						out.print(pageNum + 1);
						if (isSearch) {
							out.print("&game_name=" + game_name);
							out.print("&genresList=" + game_genre);
							out.print("&min_main=" + minTime);
							out.print("&max_main=" + maxTime);
						}
						out.print("\">Next</a></li>");
					} else {
						out.print("<li class=\"disabled\"><a href=\"#\">Next</a></li>");
					}
					out.print("</ul>");
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