<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.util.List"%>
<%@page import="gh.datamodel.Entry"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="java.sql.Timestamp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/admin.css" rel="stylesheet">
<title>All Gameplay Entries</title>
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
			<div class="alert alert-success text-center withmargins">Gameplay
				Entries (Longest Combined Gameplays First)</div>
			<%
				String pageNumStr = request.getParameter("page");
				int pageNum;
				int limit = 13;
				if (pageNumStr != null)
					pageNum = Integer.parseInt(pageNumStr);
				else
					pageNum = 1;
				List<Entry> entries = catalog.getEntries(pageNum, limit);
				for (int i = 0; i < entries.size(); ++i) {
					Entry curr = entries.get(i);
					int entry_id = curr.getID();
					int game_id = curr.getGameID();
					String platform = curr.getPlatform();
					Timestamp subDate = curr.getSubmissionDate();
					GameplayTimes times = curr.getGameplayTimes();
					Game game = catalog.getGame(game_id);
					out.print("<div class=\"alert alert-success\">");
					out.print("<a href=\"entry.jsp?id=" + entry_id
							+ "\" class=\"alert-link\">");
					out.print("<span class=\"glyphicon glyphicon-ok-sign\"></span>");
					out.print(" " + game.getName() + ";");
					out.print(" GAMER: " + curr.getUsername() + ";");
					out.print(" On: " + platform + ";");
					out.print(" HOURS: <span class=\"label label-primary\">" + times + "</span>");
					out.print(" SUBMITTED: " + subDate + ";");
					out.print("</a>");
					out.print("</div>");
				}
				out.print("<ul class=\"pager\">");
				int entriesSoFar = (pageNum - 1) * limit + entries.size();
				int entriesNum = catalog.getEntriesQuantity();
				if (entriesNum > entries.size()) {
					if (pageNum == 1) {
						out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
					} else {
						out.print("<li><a href=\"entries.jsp?page=");
						out.print(pageNum - 1 + "\">Previous</a></li>");
					}
					if (entriesSoFar <= limit) {
						out.print("<li><a href=\"entries.jsp?page=");
						out.print(pageNum + 1 + "\">Next</a></li>");
					} else {
						out.print("<li class=\"disabled\"><a href=\"entries.jsp?page=");
						out.print(pageNum + 1 + "\">Next</a></li>");
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