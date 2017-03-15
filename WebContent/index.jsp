<%@page import="gh.datamodel.Account"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.db.managers.GameCatalog" %>
<%@page import="gh.db.managers.Statistics" %>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="gh.datamodel.Game" %>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="html/header.html"%>
<title>GameHours</title>
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
					<li class="active"><a href="index.jsp">MAIN</a></li>
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
	GameCatalog catalog = (GameCatalog) getServletContext()
		.getAttribute(GameCatalog.ATTRIBUTE_NAME);
	Statistics stat = (Statistics) getServletContext()
		.getAttribute(Statistics.ATTRIBUTE_NAME);	
	%>
	<div class="jumbotron">
		<div class="container">
			<%
				if (loggedInUser == null)
					out.print("<h2 class=\"text-center\"><span class=\"label label-warning\">Please Register for Full Experience</span></h2>");
			%>
			<h2 class="text-center">
				<span class="label label-info">Top Games This Month</span>
			</h2>
				<%
				List<Game>  games = stat.getPopularGames(1,8);
				for(int i = 0; i < games.size(); ++i) {
					if(i % 2 == 0 && games.size() >= 2) {
						out.print("<div class=\"row\">");
					}
					out.print("<div class=\"col-md-6\">");
					out.print("<div class=\"row-center\">");
					Game game = games.get(i);
					String gameName = game.getName();
					out.print("<a href=\"game.jsp?game=" + game.getID() + "\">");
					out.print("<h4><span class=\"label label-success\">" + gameName + ""+"</span></h4>");
					out.print("</div>");
					out.print("<div class=\"col-md-4\">");
					String gameCover = games.get(i).getImageName();
					out.print("<a href=\"game.jsp?game=" + game.getID() + "\">");
					out.print("<img class=\"img img-rounded block-center\" src=\"GetImage?image=game_covers/thumbnail_120x120/" + gameCover + "\"></a>");
					out.print("</div>");
					out.print("<div class=\"col-md-8\">");
					out.print("<ul class=\"list-group\">");
					Integer main = stat.getAverageGameplayTimes(game).getMainGameplayTime();
					Integer extra = stat.getAverageGameplayTimes(game).getExtraGameplayTime();
					Integer complete = stat.getAverageGameplayTimes(game).getCompleteGameplayTime();
					out.print("<li class=\"list-group-item list-group-item-success\">");
					out.print("<span class=\"badge\">" + main + ""+"</span>");
					out.print("MAIN HOURS");
					out.print("</li>");
					out.print("<li class=\"list-group-item list-group-item-info\">");
					out.print("<span class=\"badge\">" + extra + ""+"</span>");
					out.print("EXTRA HOURS");
					out.print("</li>");
					out.print("<li class=\"list-group-item list-group-item-danger\">");
					out.print("<span class=\"badge\">" + complete + ""+"</span>");
					out.print("COMPLETE HOURS");
					out.print("</li>");
					out.print("</ul>");
					out.print("</div>");
					out.print("</div>");
					if((i + 1) < games.size()) {
						i++;
						out.print("<div class=\"col-md-6\">");
						out.print("<div class=\"row-center\">");
						Game game1 = games.get(i);
						String gameName1 = game1.getName();
						out.print("<a href=\"game.jsp?game=" + game1.getID() + "\">");
						out.print("<h4><span class=\"label label-success\">" + gameName1 + ""+"</span></h4>");
						out.print("</div>");
						out.print("<div class=\"col-md-4\">");
						String gameCover1 = game1.getImageName();
						out.print("<a href=\"game.jsp?game=" + game1.getID() + "\">");
						out.print("<img class=\"img img-rounded block-center\" src=\"GetImage?image=game_covers/thumbnail_120x120/" + gameCover1 + "\"></a>");
						out.print("</div>");
						out.print("<div class=\"col-md-8\">");
						out.print("<ul class=\"list-group\">");
						Integer main1 = stat.getAverageGameplayTimes(game1).getMainGameplayTime();
						Integer extra1 = stat.getAverageGameplayTimes(game1).getExtraGameplayTime();
						Integer complete1 = stat.getAverageGameplayTimes(game1).getCompleteGameplayTime();
						out.print("<li class=\"list-group-item list-group-item-success\">");
						out.print("<span class=\"badge\">" + main1 + ""+"</span>");
						out.print("MAIN HOURS");
						out.print("</li>");
						out.print("<li class=\"list-group-item list-group-item-info\">");
						out.print("<span class=\"badge\">" + extra1 + ""+"</span>");
						out.print("EXTRA HOURS");
						out.print("</li>");
						out.print("<li class=\"list-group-item list-group-item-danger\">");
						out.print("<span class=\"badge\">" + complete1 + ""+"</span>");
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