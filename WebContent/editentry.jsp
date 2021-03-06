<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Entry"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/jumbotron-narrow.css" rel="stylesheet">
<title>Edit Gameplay Entry</title>
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
			<form action="UpdateEntry" method="post" role="form">
				<%
					Entry currEntry = catalog.getEntry(entry_id);
					if (currEntry == null || !currEntry.getUsername().equals(loggedInUser) && !manager.isAdmin(acc)) {
						response.sendRedirect("index.jsp");
						return;
					}
					Game game = catalog.getGame(currEntry.getGameID());
					GameplayTimes times = currEntry.getGameplayTimes();
				%>
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
					<div class="panel-body">
						<select id="platformsList" class="form-control" name="platflist">
							<%
								List<String> platforms = game.getPlatforms();
								for (int i = 0; i < platforms.size(); ++i)
									out.print("<option value=\"" + platforms.get(i) + "\">" + platforms.get(i) + "</option>");
							%>
						</select> <input type="hidden" class="form-control" id="platform_hidden"
							name="platform">
					</div>
				</div>
				<div class="panel panel-default">
					<div class="panel-body">At least one of the HOURS should be filled in!</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Main Story Gameplay HOURS</h3>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<div class="col-sm-12">
								<input type="text" class="form-control"
									placeholder="Your Main Story Gameplay HOURS"
									name="main" value=<%=times.getMainGameplayTime()%>>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Extra Gameplay HOURS</h3>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<div class="col-sm-12">
								<input type="text" class="form-control"
									placeholder="Your Extra Gameplay HOURS"
									name="extra" value=<%=times.getExtraGameplayTime()%>>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">OCD (Ideal Gameplay) HOURS</h3>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<div class="col-sm-12">
								<input type="text" class="form-control"
									placeholder="Your Complete Ideal Gameplay HOURS"
									name="complete" value=<%=times.getCompleteGameplayTime()%>>
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" name="toPage"
					value="entry.jsp?id=<%=entry_id%>" /> <input type="hidden"
					name="entry_id" value="<%=entry_id%>" /> <input type="hidden"
					name="game_id" value="<%=game.getID()%>" />
				<button class="btn btn-lg btn-warning submit_button" type="submit">UPDATE</button>
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
	<%
		out.print("<script>");
			out.print("$(\"#platformsList\").val(");
			out.print("\"" + currEntry.getPlatform() + "\"");
			out.print(");");
			out.print("</script>");
	%>
	<script type="text/javascript">
		$("#platform_hidden").val($("#platformsList").find(":selected").text());
		$(document).ready(
				function() {
					$("#platformsList").change(
							function() {
								$("#platform_hidden").val(
										$("#platformsList").find(":selected")
												.text());
							});
				});
	</script>
</body>
</html>