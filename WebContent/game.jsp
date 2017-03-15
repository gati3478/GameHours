<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.datamodel.Entry"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.datamodel.Review"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="gh.db.managers.Statistics"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/game.css" rel="stylesheet">
<%
	GameCatalog catalog = (GameCatalog) getServletContext()
			.getAttribute(GameCatalog.ATTRIBUTE_NAME);
	Statistics stats = (Statistics) getServletContext().getAttribute(
			Statistics.ATTRIBUTE_NAME);
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
<title><%=game.getName()%></title>
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
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		Account acc = manager.getAccount(loggedInUser);
	%>
	<div class="jumbotron">
		<div class="container-fluid">
			<div class="col-md-3">
				<div class="row">
					<%
						String imageFile = "default.png";
						if (game.getImageName() != null)
							imageFile = game.getImageName();
					%>
					<img class="img-rounded center-block bigthumb"
						src="GetImage?image=game_covers/standard/<%=imageFile%>">
				</div>
				<div class="row text-center withmargins">
					<%
						if (acc != null && manager.isAdmin(acc)) {
							out.print("<a class=\"btn btn-info text-center\" href=\"setcoverimage.jsp?game="
									+ gameId + "\">Change Cover Image");
							out.print("</a>");
						}
					%>
				</div>
				<div class="row text-center withmargins">
					<a class="btn btn-warning btn-block text-center"
						href="gamereviews.jsp?game=<%=game_id_str%>">Read Reviews</a>
				</div>
				<div class="row text-center withmargins">
					<a class="btn btn-warning btn-block text-center"
						href="images.jsp?game=<%=game_id_str%>">View Images</a>
				</div>
				<div class="row text-center withmargins">
					<a class="btn btn-warning btn-block text-center"
						href="videos.jsp?game=<%=game_id_str%>">View Videos</a>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Game</h3>
						</div>
						<div class="panel-body"><%=game.getName()%></div>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Developers</h3>
						</div>
						<%
							if (game.getName() != null)
								out.print("<div class=\"panel-body\">" + game.getDevelopers()
										+ "</div>");
							else
								out.print("<div class=\"panel-body\">Not Specified</div>");
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Publishers</h3>
						</div>
						<%
							if (game.getName() != null)
								out.print("<div class=\"panel-body\">" + game.getPublishers()
										+ "</div>");
							else
								out.print("<div class=\"panel-body\">Not Specified</div>");
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Genre(s)</h3>
						</div>
						<%
							List<String> genres = game.getGenres();
							if (genres.size() != 0) {
								out.print("<div class=\"panel-body\">");
								for (int i = 0; i < genres.size(); ++i) {
									out.print(genres.get(i));
									if (i != genres.size() - 1)
										out.print(", ");
								}
								out.print("</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Platform(s)</h3>
						</div>
						<%
							List<String> platforms = game.getPlatforms();
							if (platforms.size() != 0) {
								out.print("<div class=\"panel-body\">");
								for (int i = 0; i < platforms.size(); ++i) {
									out.print(platforms.get(i));
									if (i != platforms.size() - 1)
										out.print(", ");
								}
								out.print("</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Release Date</h3>
						</div>
						<%
							if (game.getReleaseDate() != null)
								out.print("<div class=\"panel-body\">" + game.getReleaseDate()
										+ "</div>");
							else
								out.print("<div class=\"panel-body\">Not Specified</div>");
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Short Description</h3>
						</div>
						<%
							if (game.getShortDescription() != null)
								out.print("<div class=\"panel-body\">"
										+ game.getShortDescription() + "</div>");
							else
								out.print("<div class=\"panel-body\">Not Specified</div>");
						%>
					</div>
				</div>
				<div class="row text-center withmargins">
					<%
						if (acc != null && manager.isAdmin(acc)) {
							out.print("<a class=\"btn btn-info btn-block withmargins\" href=\"editgame.jsp?game="
									+ game.getID() + "\">Update Game Info");
							out.print("</a>");
							out.print("<form action=\"DeleteGame\" method=\"post\">");
							out.print("<input type=\"hidden\" name=\"game_id\" value=\""
									+ game_id_str + "\">");
							out.print("<button type=\"submit\" class=\"btn btn-danger btn-block withmargins\">Delete Game</button>");
							out.print("</form>");
						}
					%>
				</div>
			</div>
			<div class="col-md-9">
				<div class="row">
					<%
						if (acc != null && !manager.isBanned(acc)) {
							if (catalog.getEntry(acc, game) == null) {
								out.print("<a href=\"addentry.jsp?game=" + game_id_str);
								out.print("\" class=\"btn btn-danger btn-block btn-lg withmargins2\">Submit My Gameplay Entry!</a>");
							} else {
								Entry entry = catalog.getEntry(acc, game);
								out.print("<a href=\"entry.jsp?id=" + entry.getID());
								out.print("\" class=\"btn btn-danger btn-block btn-lg withmargins2\">View My Gameplay Entry!</a>");
							}
						}
					%>
				</div>
				<div class="row">
					<div class="panel panel-danger withmargins2">
						<div class="panel-heading">
							<h3 class="text-center">
								<span class="label label-danger">Stats for <%=game.getName()%></span>
							</h3>
						</div>
						<div class="panel-body">
							<div class="row">
								<%
									int rating = stats.getAverageRating(game);
									String labelClass = "label";
									if (rating >= 85)
										labelClass += " label-success";
									else if (rating >= 60)
										labelClass += " label-info";
									else if (rating >= 50)
										labelClass += " label-warning";
									else
										labelClass += " label-danger";
								%>
								<h1 class="text-center">
									<span class="<%=labelClass%>"><%=rating%>% Rating</span>
								</h1>
							</div>
							<div class="row withmargins">
								<h2 class="text-center">
									<span class="label label-primary">Average HOURS</span>
								</h2>
							</div>
							<div class="row withmargins">
								<%
									GameplayTimes times = stats.getAverageGameplayTimes(game);
								%>
								<div class="col-md-3">
									<div class="panel panel-primary">
										<div class="panel-heading">
											<h3 class="panel-title">Main Story Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=times.getMainGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-primary">
										<div class="panel-heading">
											<h3 class="panel-title">Extra Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=times.getExtraGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-primary">
										<div class="panel-heading">
											<h3 class="panel-title">OCD (Ideal) Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=times.getCompleteGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-primary">
										<div class="panel-heading">
											<h3 class="panel-title">Combined Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=times.getAverageCombinedTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
							</div>
							<div class="row withmargins">
								<h2 class="text-center">
									<span class="label label-success">Minimum HOURS</span>
								</h2>
							</div>
							<div class="row withmargins">
								<%
									GameplayTimes mins = stats.getMinGameplayTimes(game);
								%>
								<div class="col-md-3">
									<div class="panel panel-success">
										<div class="panel-heading">
											<h3 class="panel-title">Main Story Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=mins.getMainGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-success">
										<div class="panel-heading">
											<h3 class="panel-title">Extra Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=mins.getExtraGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-success">
										<div class="panel-heading">
											<h3 class="panel-title">OCD (Ideal) Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=mins.getCompleteGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-success">
										<div class="panel-heading">
											<h3 class="panel-title">Combined Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=mins.getAverageCombinedTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
							</div>
							<div class="row withmargins">
								<h2 class="text-center">
									<span class="label label-warning">Maximum HOURS</span>
								</h2>
							</div>
							<div class="row withmargins">
								<%
									GameplayTimes maxs = stats.getMaxGameplayTimes(game);
								%>
								<div class="col-md-3">
									<div class="panel panel-warning">
										<div class="panel-heading">
											<h3 class="panel-title">Main Story Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=maxs.getMainGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-warning">
										<div class="panel-heading">
											<h3 class="panel-title">Extra Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=maxs.getExtraGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-warning">
										<div class="panel-heading">
											<h3 class="panel-title">OCD (Ideal) Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=maxs.getCompleteGameplayTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
								<div class="col-md-3">
									<div class="panel panel-warning">
										<div class="panel-heading">
											<h3 class="panel-title">Combined Gameplay</h3>
										</div>
										<div class="panel-body text-center">
											<h4><%=maxs.getAverageCombinedTime()%>
												HOURS
											</h4>
										</div>
									</div>
								</div>
							</div>
							<div class="row withmargins">
								<%
									Account fast = stats.getFastestUserByGame(game);
									if (fast != null) {
										out.print("<div class=\"col-md-6\">");
										out.print("<div class=\"panel panel-info\">");
										out.print("<div class=\"panel-heading\">");
										out.print("<h3 class=\"panel-title\">");
										out.print("<h3 class=\"text-center\">");
										out.print("<span class=\"label label-info\">Speedrunner</span>");
										out.print("</h3>");
										out.print("</h3>");
										out.print("</div>");
										out.print("<div class=\"panel-body\">");
										out.print("<div class=\"col-md-6\">");
										if (loggedInUser != null)
											out.print("<a href=\"profile.jsp?username="
													+ fast.getUsername() + "\">");
										String imgSrc = fast.getAvatarFilename();
										if (imgSrc == null)
											imgSrc = "default.jpg";
										out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/standard_184x184/"
												+ imgSrc + "\">");
										if (loggedInUser != null)
											out.print("</a>");
										out.print("</div>");
										out.print("<div class=\"col-md-6\">");
										GameplayTimes fastest = catalog.getEntry(fast, game)
												.getGameplayTimes();
										out.print("<ul class=\"list-group\">");
										out.print("<li class=\"list-group-item list-group-item-danger\">");
										if (loggedInUser != null)
											out.print("<a href=\"profile.jsp?username="
													+ fast.getUsername() + "\">");
										if (fast.getNickname() != null)
											out.print(fast.getNickname());
										else
											out.print(fast.getUsername());
										if (loggedInUser != null)
											out.print("</a>");
										out.print("</li>");
										out.print("<li class=\"list-group-item list-group-item-info\">Main: "
												+ fastest.getMainGameplayTime() + " HOURS</li>");
										out.print("<li class=\"list-group-item list-group-item-info\">Extra: "
												+ fastest.getExtraGameplayTime() + " HOURS</li>");
										out.print("<li class=\"list-group-item list-group-item-info\">OCD: "
												+ fastest.getCompleteGameplayTime() + " HOURS</li>");
										out.print("</ul>");
										out.print("</div>");
										out.print("</div>");
										out.print("</div>");
									}
								%>
							</div>
							<%
								Account slow = stats.getSlowestUserByGame(game);
								if (slow != null) {
									out.print("<div class=\"col-md-6\">");
									out.print("<div class=\"panel panel-info\">");
									out.print("<div class=\"panel-heading\">");
									out.print("<h3 class=\"panel-title\">");
									out.print("<h3 class=\"text-center\">");
									out.print("<span class=\"label label-info\">Explorer</span>");
									out.print("</h3>");
									out.print("</h3>");
									out.print("</div>");
									out.print("<div class=\"panel-body\">");
									out.print("<div class=\"col-md-6\">");
									if (loggedInUser != null)
										out.print("<a href=\"profile.jsp?username="
												+ slow.getUsername() + "\">");
									String imgSrc = slow.getAvatarFilename();
									if (imgSrc == null)
										imgSrc = "default.jpg";
									out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/standard_184x184/"
											+ imgSrc + "\">");
									if (loggedInUser != null)
										out.print("</a>");
									out.print("</div>");
									out.print("<div class=\"col-md-6\">");
									GameplayTimes slowest = catalog.getEntry(slow, game)
											.getGameplayTimes();
									out.print("<ul class=\"list-group\">");
									out.print("<li class=\"list-group-item list-group-item-danger\">");
									if (loggedInUser != null)
										out.print("<a href=\"profile.jsp?username="
												+ slow.getUsername() + "\">");
									if (slow.getNickname() != null)
										out.print(slow.getNickname());
									else
										out.print(slow.getUsername());
									if (loggedInUser != null)
										out.print("</a>");
									out.print("</li>");
									out.print("<li class=\"list-group-item list-group-item-info\">Main: "
											+ slowest.getMainGameplayTime() + " HOURS</li>");
									out.print("<li class=\"list-group-item list-group-item-info\">Extra: "
											+ slowest.getExtraGameplayTime() + " HOURS</li>");
									out.print("<li class=\"list-group-item list-group-item-info\">OCD: "
											+ slowest.getCompleteGameplayTime() + " HOURS</li>");
									out.print("</ul>");
									out.print("</div>");
									out.print("</div>");
									out.print("</div>");
								}
							%>
						</div>
					</div>
					<div class="row withmargins">
						<div class="panel panel-danger" id="platform-stats">
							<div class="panel-heading">
								<h3 class="panel-title">
									<h2 class="text-center">
										<span class="label label-danger">Platform Stats</span>
									</h2>
								</h3>
							</div>
							<div class="panel-body text-center">
								<table class="table">
									<tr>
										<td><h4>
												<span class="label label-danger">Platform</span>
											</h4></td>
										<td><h4>
												<span class="label label-danger">% of total gameplays</span>
											</h4></td>
										<td><h4>
												<span class="label label-danger">Quantity</span>
											</h4></td>
									</tr>
									<%
										Map<String, Integer> platfromStats = stats.getPlatformStats(game);
										int totalPlays = 0;
										for (String plat : platfromStats.keySet())
											totalPlays += platfromStats.get(plat).intValue();
										for (String plat : platfromStats.keySet()) {
											Integer currQuantity = platfromStats.get(plat);
											double percent = (double) currQuantity * 100 / totalPlays;
											String successClass = "progress-bar";
											out.print("<tr>");
											out.print("<td>" + plat + "</td>");
											out.print("<td>");
											if (percent >= 75)
												successClass += " progress-bar-success";
											else if (percent >= 50)
												successClass += " progress-bar-info";
											else if (percent >= 25)
												successClass += " progress-bar-warning";
											else
												successClass += " progress-bar-danger";
											out.print("<div class=\"progress\">");
											out.print("<div class=\"" + successClass + "\"");
											out.print(" role=\"progressbar\" aria-valuenow=\""
													+ currQuantity + "\"");
											out.print(" aria-valuemin=\"0\" aria-valuemax=\"" + totalPlays
													+ "\"");
											out.print(" style=\"width: +" + percent + "%\">");
											out.print("<span class=\"sr-only\">" + percent + "%</span>");
											out.print("</div>");
											out.print("</div>");
											out.print("</td>");
											out.print("<td>" + currQuantity + "</td>");
											out.print("</tr>");
										}
									%>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
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