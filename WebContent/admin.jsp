<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/admin.css" rel="stylesheet">
<title>Profile</title>
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
			<div class="row text-center withmargins">
				<div class="alert alert-success">Admin Panel</div>
				<div class="row">
					<div class="col-md-6" id="button1">
						<a href="addgame.jsp" class="btn btn-primary hyperspan">
							<h2 class="div_btn_label">ADD GAME</h2>
						</a>
					</div>
					<div class="col-md-6" id="button2">
						<a href="users.jsp" class="btn btn-warning hyperspan">
							<h2 class="div_btn_label">BROWSE USERS</h2>
						</a>
					</div>
				</div>
				<div class="row">
					<div class="col-md-6" id="button3">
						<a href="entries.jsp" class="btn btn-success hyperspan">
							<h2 class="div_btn_label">BROWSE ENTRIES</h2>
						</a>
					</div>
					<div class="col-md-6" id="button4">
						<a href="reviews.jsp" class="btn btn-info hyperspan">
							<h2 class="div_btn_label">BROWSE REVIEWS</h2>
						</a>
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