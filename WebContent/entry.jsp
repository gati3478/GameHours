<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Entry"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.db.managers.GameCatalog"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/jumbotron-narrow.css" rel="stylesheet">
<title>Gameplay Entry</title>
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
		String entry_id_str = request.getParameter("id");
		Integer entry_id = new Integer(0);
		Account acc = null;
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
		if (loggedInUser == null || entry_id_str == null) {
			response.sendRedirect("index.jsp");
			return;
		} else {
			acc = manager.getAccount(loggedInUser);
			entry_id = Integer.parseInt(entry_id_str);
		}
	%>
	<div class="container not_nav">
		<div class="jumbotron not_nav">
			<form action="DeleteEntry" method="post" role="form">
				<%
					Entry currEntry = catalog.getEntry(entry_id);
					if (currEntry == null) {
						response.sendRedirect("index.jsp");
						return;
					}
					Game game = catalog.getGame(currEntry.getGameID());
					GameplayTimes times = currEntry.getGameplayTimes();
					Account author = manager.getAccount(currEntry.getUsername());
				%>
				<div class="panel panel-success">
					<div class="panel-heading">
						<h3 class="panel-title">Gamer</h3>
					</div>
					<div class="panel-body">
						<%
							if (author.getNickname() != null) {
								out.print("<p>" + author.getNickname() + "</p>");
							} else {
								out.print("<p>" + author.getUsername() + "</p>");
							}
						%>
						<a href="profile.jsp?username=<%=author.getUsername()%>"
							class="thumbnail"> <img data-src="holder.js/100x180"
							class="img img-rounded"
							<%if (author.getAvatarFilename() == null)
				out.print("src=\"GetImage?image=avatars/standard_184x184/default.jpg\"");
			else
				out.print("src=\"GetImage?image=avatars/standard_184x184/"
						+ author.getAvatarFilename() + "\"");%>>
						</a>
					</div>
				</div>
				<div class="panel panel-success">
					<div class="panel-heading">
						<h3 class="panel-title">Game</h3>
					</div>
					<div class="panel-body">
						<p><%=game.getName()%></p>
						<a href="game.jsp?game=<%=game.getID()%>" class="thumbnail"> <img
							data-src="holder.js/100x180" class="img img-rounded"
							src="GetImage?image=game_covers/standard/<%=game.getImageName()%>">
						</a>
					</div>
				</div>
				<div class="panel panel-success">
					<div class="panel-heading">
						<h3 class="panel-title">Platform</h3>
					</div>
					<div class="panel-body"><%=currEntry.getPlatform()%></div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Main Story Gameplay HOURS</h3>
					</div>
					<div class="panel-body"><%=times.getMainGameplayTime()%>
						HOURS
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Extra Gameplay HOURS</h3>
					</div>
					<div class="panel-body"><%=times.getExtraGameplayTime()%>
						HOURS
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">OCD (Ideal Gameplay) HOURS</h3>
					</div>
					<div class="panel-body"><%=times.getCompleteGameplayTime()%>
						HOURS
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Combined</h3>
					</div>
					<div class="panel-body"><%=times.getAverageCombinedTime()%>
						HOURS
					</div>
				</div>
				<div class="panel panel-warning">
					<div class="panel-heading">
						<h3 class="panel-title">Submission Date</h3>
					</div>
					<div class="panel-body"><%=currEntry.getSubmissionDate().toString()%></div>
				</div>
				<input type="hidden" name="toPage"
					value="game.jsp?game=<%=game.getID()%>" /> <input type="hidden"
					name="entry_id" value="<%=entry_id%>" />
				<%
					if (!manager.isBanned(acc)
							&& author.getUsername().equals(acc.getUsername())
							|| manager.isAdmin(acc)) {
						out.print("<a href=\"editentry.jsp?id="
								+ entry_id
								+ "\" class=\"btn btn-lg btn-warning submit_button withmarginbtn\">EDIT</a>");

						out.print("<button class=\"btn btn-lg btn-danger submit_button withmarginbtn\" type=\"submit\">DELETE</button>");
					}
				%>
			</form>
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