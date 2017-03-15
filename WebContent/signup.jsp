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
<title>Sign Up</title>
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
		<form action="SignUp" method="post"
			class="form-horizontal form-signin" id="gh_sign" role="form"
			accept-charset="UTF-8">
			<%
				String usernameTaken = (String) request
						.getAttribute("usernameTaken");
				String error = (String) request.getAttribute("error");
				if (usernameTaken != null)
					out.print("<h2 class=\"form-signin-heading text-center\"><span class=\"label label-warning\">Account with such username already exists</span></h2>");
				else if (error != null)
					out.print("<h2 class=\"form-signin-heading text-center\"><span class=\"label label-warning\">Please, try again</span></h2>");
				else
					out.print("<h2 class=\"form-signin-heading text-center\"><span class=\"label label-success\">Sign Up Here</span></h2>");
			%>
			<div class="form-group">
				<label for="inputName3" class="col-sm-2 control-label">Username:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control" placeholder="ID" required
						autofocus id="username" name="username">
				</div>
			</div>
			<div class="form-group">
				<label for="inputPassword3" class="col-sm-2 control-label">Password:</label>
				<div class="col-sm-10">
					<input type="password" class="form-control" placeholder="Password"
						required id="password" name="password">
				</div>
			</div>
			<div class="form-group">
				<label for="inputEmail3" class="col-sm-2 control-label">E-mail:</label>
				<div class="col-sm-10">
					<input type="email" class="form-control"
						placeholder="Your E-mail Address" required id="email" name="email">
				</div>
			</div>
			<div class="form-group">
				<label for="inputNickname3" class="col-sm-2 control-label">Nickname:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Preffered Nickname (Optional)" id="nickname"
						name="nickname">
				</div>
			</div>
			<div class="form-group">
				<label for="inputFirstName3" class="col-sm-2 control-label">First
					Name:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Your First name (Optional)" id="firstname"
						name="firstname">
				</div>
			</div>
			<div class="form-group">
				<label for="inputLastName3" class="col-sm-2 control-label">Last
					Name:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Your Last Name (Optional)" id="lastname"
						name="lastname">
				</div>
			</div>
			<div class="form-group">
				<label for="inputBirthdate3" class="col-sm-2 control-label">Birthdate:</label>
				<div class="col-sm-10">
					<input type="date" class="form-control input-sm"
						placeholder="01/01/1994  (Optional)" id="birthdate"
						name="birthdate">
				</div>
			</div>
			<div class="form-group">
				<label for="inputGender3" class="col-sm-2 control-label">Gender:</label>
				<div class="col-sm-10">
					<%@include file="html/genders.html"%>
				</div>
			</div>
			<div class="form-group">
				<label for="inputCountry3" class="col-sm-2 control-label">Country:</label>
				<div class="col-sm-10">
					<%@include file="html/countries.html"%>
				</div>
			</div>
			<div class="form-group">
				<label for="inputSteamID3" class="col-sm-2 control-label">Steam
					ID:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Your Steam Alias (Optional)" id="steam_id"
						name="steam_id">
				</div>
			</div>
			<div class="form-group">
				<label for="inputPSNID3" class="col-sm-2 control-label">PlayStation
					Network ID:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Your PlayStation Network ID (Optional)" id="psn_id"
						name="psn_id">
				</div>
			</div>
			<div class="form-group">
				<label for="inputXbox3" class="col-sm-2 control-label">Xbox
					Live ID:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control"
						placeholder="Your Xbox Live Gamertag (Optional)" id="xbox_id"
						name="xbox_id">
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button class="btn btn-lg btn-success btn-block" type="submit">Sign
						Up</button>
				</div>
			</div>
		</form>
		<div class="facebookgoogle">
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
				Sign Up</a>
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
	<script type="text/javascript">
		$("#gender_hidden").val($("#genderList").find(":selected").text());
		$(document).ready(
				function() {
					$("#genderList").change(
							function() {
								$("#gender_hidden").val(
										$("#genderList").find(":selected")
												.text());
							});
				});

		$("#country_hidden").val($("#countriesList").find(":selected").text());
		$(document).ready(
				function() {
					$("#countriesList").change(
							function() {
								$("#country_hidden").val(
										$("#countriesList").find(":selected")
												.text());
							});
				});
	</script>
	<%@include file="html/googlesign.html"%>
</body>
</html>