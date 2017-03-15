<%@page import="gh.datamodel.Account"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@include file="html/header.html"%>
<!-- Custom styles for this page -->
<link href="styles/signin.css" rel="stylesheet">
<title>Sign In</title>
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
		if (loggedInUser != null) {
			response.sendRedirect("index.jsp");
			return;
		}
	%>
	<div class="container">
		<form action="SignIn" method="post" class="form-signin" id="gh_sign"
			role="form">
			<%
				String incorrectID = (String) request
						.getAttribute("incorrectUsername");
				String incorrectPass = (String) request
						.getAttribute("incorrectPassword");
				if (incorrectID != null || incorrectPass != null)
					out.print("<h2 class=\"form-signin-heading text-center\"><span class=\"label label-warning\">Please try again</span></h2>");
				else
					out.print("<h2 class=\"form-signin-heading text-center\"><span class=\"label label-success\">Please Sign In</span></h2>");
			%>
			<input type="text" class="form-control" placeholder="ID" required
				autofocus id="username" name="username"> <input
				type="password" class="form-control" placeholder="Password" required
				id="password" name="password">
			<button class="btn btn-lg btn-success btn-block" type="submit">Sign
				in</button>
		</form>
		<div class="form-signin">
			<%
				String AppID = "323918654424794";
				String redirectURL = "http://localhost:8080/GameHours/FacebookSign";
				String fbURL = "http://www.facebook.com/dialog/oauth?client_id="
						+ AppID + "&redirect_uri="
						+ URLEncoder.encode(redirectURL, "UTF-8")
						+ "&scope=public_profile,email";
			%>
			<a href="<%=fbURL%>"
				class="btn btn-lg btn-primary btn-block form-signin">Facebook
				Sign In</a>
			<button class="g-signin btn btn-lg btn-danger btn-block form-signin"
				id="signinButton"
				data-scope="https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read"
				data-requestvisibleactions="http://schemas.google.com/AddActivity"
				data-clientId="290990354966-7poh9j7rpsph7jclk4fjfspsf31gcfu3.apps.googleusercontent.com"
				data-callback="onSignInCallback" data-theme="dark"
				data-cookiepolicy="single_host_origin">Google+ Sign Up</button>
		</div>
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
	<%@include file="html/googlesign.html"%>
</body>
</html>