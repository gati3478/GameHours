<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.util.List"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.db.managers.GameCatalog"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/images.css" rel="stylesheet">
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
<title><%=game.getName()%> Images</title>
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
						if (acc != null && manager.isAdmin(acc)) {
							out.print("<a class=\"btn btn-warning text-center\" ");
							out.print("href=\"addimage.jsp?game=");
							out.print(game_id_str);
							out.print("\">Add New Image</a>");
						}
					%>
				</div>
			</div>
			<div class="col-md-9">
				<div class="alert alert-info text-center withmargins"><%=game.getName()%>
					Images
				</div>
				<%
					List<String> images = catalog.getImages(game);
					for (int i = 0; i < images.size(); ++i) {
						out.print("<div class=\"row\">");
						out.print("<form action=\"DeleteImage\" method=\"post\">");
						out.print("<img class=\"img img-rounded block-center screenshot\" src=\"GetImage?image=game_images/"
								+ images.get(i) + "\">");
						out.print("<input type=\"hidden\" value=\"images.jsp?game="
								+ game_id_str + "\" ");
						out.print("name=\"toPage\">");
						out.print("<input type=\"hidden\" value=\"" + images.get(i)
								+ "\" ");
						out.print("name=\"image\">");
						out.print("<input type=\"hidden\" value=\"" + game_id_str
								+ "\" ");
						out.print("name=\"game\">");
						if (acc != null && manager.isAdmin(acc)) {
							out.print("<button type=\"submit\" class=\"btn btn-danger btn-block\">DELETE IMAGE</button>");
						}
						out.print("</form>");
						out.print("</div>");
					}
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