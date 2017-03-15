<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.util.Hash"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/jumbotron-narrow.css" rel="stylesheet">
<title>Account Deletion</title>
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
		}
	%>
	<div class="container not_nav">
		<div class="jumbotron not_nav">
			<h2 class="text-center">Do You Really Want to Leave US?!</h2>
			<form action="DeleteAccount" method="post" role="form">
				<%
					if (!acc.getHashedPassword().equals(
							Hash.hashText(acc.getUsername() + acc.getEmail() + "salt"))) {
						out.print("<input type=\"password\" class=\"form-control\"");
						out.print(" placeholder=\"Type your password for confirmation...\"");
						out.print("required id=\"password\" name=\"password\">");
					}
				%>
				<input type="hidden" name="toPage" value="index.jsp" /> <a
					href="profile.jsp" class="btn btn-lg btn-success submit_button">NO!</a>
				<button class="btn btn-lg btn-danger submit_button" type="submit"
					value="DELETE">YES</button>
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