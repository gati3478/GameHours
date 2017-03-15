<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Date"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/admin.css" rel="stylesheet">
<title>All Registered Users</title>
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
		AccountManager manager = null;
		if (loggedInUser == null) {
			response.sendRedirect("index.jsp");
			return;
		} else {
			manager = (AccountManager) getServletContext().getAttribute(
					AccountManager.ATTRIBUTE_NAME);
			acc = manager.getAccount(loggedInUser);
			if (!manager.isAdmin(acc)) {
				response.sendRedirect("index.jsp");
				return;
			}
		}
	%>
	<div class="jumbotron">
		<div class="container">
			<div class="alert alert-success text-center withmargins">All
				Registered Users (Banned Users Are Marked In Red)</div>
			<%
				String pageNumStr = request.getParameter("page");
				int pageNum;
				int limit = 13;
				if (pageNumStr != null)
					pageNum = Integer.parseInt(pageNumStr);
				else
					pageNum = 1;
				
				List<Account> accounts = manager.getAccounts(pageNum, limit);
				for (int i = 0; i < accounts.size(); ++i) {
					Account curr = accounts.get(i);
					String userName = curr.getUsername();
					String email = curr.getEmail();
					String nickName = curr.getNickname();
					String firstName = curr.getFirstName();
					String lastName = curr.getLastName();
					Date birthdate = curr.getBirthdate();
					String gender = curr.getGender();
					String country = curr.getCountry();
					String avatarName = curr.getAvatarFilename();
					String steamID = curr.getSteamID();
					String PSNID = curr.getPlayStationNetworkID();
					String xboxID = curr.getXboxLiveGamertag();
					if (!manager.isBanned(curr))
						out.print("<div class=\"alert alert-warning\">");
					else
						out.print("<div class=\"alert alert-danger\">");
					out.print("<a href=\"profile.jsp?username=" + userName
							+ "\" class=\"alert-link\">");
					if (avatarName == null)
						out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/default.jpg\">");
					else
						out.print("<img class=\"img img-rounded\" src=\"GetImage?image=avatars/thumbnails_44x44/"
								+ avatarName + "\">");
					out.print("<span class=\"glyphicon glyphicon-user\"></span>");
					out.print("Username: <span class=\"label label-primary\">"
							+ userName + "</span>; ");
					out.print("E-mail: <span class=\"label label-primary\">"
							+ email + "</span>; ");
					if (nickName != null)
						out.print("Nickname: <span class=\"label label-primary\">"
								+ nickName + "</span>; ");
					else
						out.print("No Nickname; ");

					if (firstName != null)
						out.print("First Name: <span class=\"label label-primary\">"
								+ firstName + "</span>; ");
					else
						out.print("No First Name; ");

					if (lastName != null)
						out.print("Last Name: <span class=\"label label-primary\">"
								+ lastName + "</span>; ");
					else
						out.print("No Last Name; ");

					if (birthdate != null)
						out.print("Birthdate: <span class=\"label label-primary\">"
								+ birthdate + "</span>; ");
					else
						out.print("No Birthdate; ");

					if (gender != null)
						out.print("Gender: <span class=\"label label-primary\">"
								+ gender + "</span>; ");
					else
						out.print("No Gender; ");

					if (country != null)
						out.print("Country: <span class=\"label label-primary\">"
								+ country + "</span>; ");
					else
						out.print("No Country; ");

					if (steamID != null)
						out.print("Steam ID: <span class=\"label label-primary\">"
								+ steamID + "</span>; ");
					else
						out.print("No Steam ID; ");

					if (PSNID != null)
						out.print("PlayStation Network ID: <span class=\"label label-primary\">"
								+ PSNID + "</span>; ");
					else
						out.print("No PlayStation Network ID; ");

					if (xboxID != null)
						out.print("Xbox Live Gamertag: <span class=\"label label-primary\">"
								+ xboxID + "</span>; ");
					else
						out.print("No Xbox Live Gamertag; ");

					out.print("</a>");
					out.print("</div>");
				}
				out.print("<ul class=\"pager\">");
				int entriesSoFar = (pageNum - 1) * limit + accounts.size();
				int entriesNum = manager.getAccountsQuantity();
				if (entriesNum > accounts.size()) {
					if (pageNum == 1) {
						out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
					} else {
						out.print("<li><a href=\"users.jsp?page=");
						out.print(pageNum - 1 + "\">Previous</a></li>");
					}
					if (entriesSoFar <= limit) {
						out.print("<li><a href=\"users.jsp?page=");
						out.print(pageNum + 1 + "\">Next</a></li>");
					} else {
						out.print("<li class=\"disabled\"><a href=\"users.jsp?page=");
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