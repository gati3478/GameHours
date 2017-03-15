<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Review"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/jumbotron-narrow.css" rel="stylesheet">
<title>Edit Review</title>
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
		String author = request.getParameter("author");
		String game_id_str = request.getParameter("game");
		Integer game_id = new Integer(0);
		Account authAcc = null;
		Account acc = null;
		Game game = null;
		AccountManager manager = (AccountManager) getServletContext()
				.getAttribute(AccountManager.ATTRIBUTE_NAME);
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
		if (loggedInUser == null || author == null || game_id == null) {
			response.sendRedirect("index.jsp");
			return;
		} else {
			acc = manager.getAccount(loggedInUser);
			authAcc = manager.getAccount(author);
			game_id = Integer.parseInt(game_id_str);
			game = catalog.getGame(game_id);
		}
	%>
	<div class="container not_nav">
		<div class="jumbotron not_nav">
			<form action="UpdateReview" method="post" role="form">
				<%
					if (authAcc == null) {
						response.sendRedirect("index.jsp");
						return;
					}
					Review currReview = catalog.getReview(authAcc, game);
					if (currReview == null) {
						response.sendRedirect("index.jsp");
						return;
					}
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
						<h3 class="panel-title">Please, Rate</h3>
					</div>
					<div class="panel-body">
						<select id="ratingsList" class="form-control" name="ratinglist">
							<%
								for (int i = 100; i >= 0; --i)
									out.print("<option value=\"" + i + "\">" + i + "</option>");
							%>
						</select> <input type="hidden" class="form-control" id="rating_hidden"
							name="rating">
					</div>
				</div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">Review</h3>
					</div>
					<div class="panel-body">
						<div class="form-group">
							<div class="col-sm-12">
								<textarea id="reviewText" class="form-control" rows="4"
									placeholder="Your Review goes here" required><%=currReview.getReviewText()%></textarea>
								<input id="reviewInput" type="hidden"
									class="form-control textarea"
									placeholder="Your review goes here" required name="reviewText"
									value="<%=currReview.getReviewText()%>">
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" name="toPage"
					value="review.jsp?author=<%=author%>&game=<%=game_id_str%>" /> <input
					type="hidden" name="author"
					value="<%=currReview.getAuthorUsername()%>" /> <input
					type="hidden" name="game" value="<%=currReview.getGameID()%>" />
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
		out.print("$(\"#ratingsList\").val(");
		out.print("\"" + currReview.getGivenRating() + "\"");
		out.print(");");
		out.print("</script>");
	%>
	<script type="text/javascript">
		$("#rating_hidden").val($("#ratingsList").find(":selected").text());
		$(document).ready(
				function() {
					$("#ratingsList").change(
							function() {
								$("#rating_hidden").val(
										$("#ratingsList").find(":selected")
												.text());
							});
				});
		$(document).ready(function() {
			$("#reviewText").change(function() {
				$("#reviewInput").val($("#reviewText").val());
			});
		});
	</script>
</body>
</html>