<%@page import="gh.util.Hash"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="html/header.html"%>
<!-- Custom styles for this page -->
<link href="styles/signup.css" rel="stylesheet">
<title>Update Your Info</title>
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
	<div class="container">
		<form action="UpdateUserPassword" method="post"
			class="form-horizontal form-signin" id="gh_sign" role="form"
			accept-charset="UTF-8">
			<%
				String error = (String) request.getAttribute("error");
				if (error != null)
					out.print("<h2 class=\"form-signin-heading text-center\">Try again</h2>");
				else
					out.print("<h2 class=\"form-signin-heading text-center\">Change Your Password Here</h2>");
			%>
			<div class="form-group">
				<%
					if (!acc.getHashedPassword().equals(
							Hash.hashText(acc.getUsername() + acc.getEmail() + "salt"))) {
						out.print("<label for=\"inputPassword3\" class=\"col-sm-2 control-label\">Old Password:</label>");
						out.print("<div class=\"col-sm-10\">");
						out.print("<input type=\"password\" class=\"form-control\" placeholder=\"Your Old Password\" required id=\"old_password\" name=\"old_password\">");
						out.print("</div>");
					}
				%>
			</div>
			<div class="form-group">
				<label for="inputPassword3" class="col-sm-2 control-label">New
					Password:</label>
				<div class="col-sm-10">
					<input type="password" class="form-control"
						placeholder="Your New Password" required id="new_password"
						name="new_password">
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button class="btn btn-lg btn-warning btn-block" type="submit">Change
						Password</button>
				</div>
			</div>
		</form>
	</div>
	<!-- /container -->
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