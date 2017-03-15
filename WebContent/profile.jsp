<%@page import="gh.db.managers.GameCatalog"%>
<%@page import="org.apache.catalina.Manager"%>
<%@page import="gh.util.Hash"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Entry"%>
<%@page import="gh.datamodel.Review"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.datamodel.GameplayTimes"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Timestamp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/profile.css" rel="stylesheet">
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
							out.print("<li class=\"active\"><a href=\"profile.jsp?username=");
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
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
		if (loggedInUser == null) {
			response.sendRedirect("index.jsp");
			return;
		} else {
			String profilePageUser = (String) request
					.getParameter("username");
			if (profilePageUser != null
					&& manager.usernameExists(profilePageUser))
				acc = manager.getAccount(profilePageUser);
			else
				acc = manager.getAccount(loggedInUser);
		}
	%>
	<div class="jumbotron">
		<div class="container">
			<div class="col-md-3">
				<div class="row">
					<%
						if (acc == null) {
							response.sendRedirect("index.jsp");
							return;
						}
						String avatarfile = "default.jpg";
						if (acc.getAvatarFilename() != null)
							avatarfile = acc.getAvatarFilename();
					%>
					<img class="img-rounded center-block bigthumb"
						src="GetImage?image=avatars/standard_184x184/<%=avatarfile%>">
				</div>
				<div class="row text-center withmargins">
					<%
						if (acc.getUsername().equals(loggedInUser)) {
							out.print("<a class=\"btn btn-info text-center\" href=\"setavatar.jsp\">Change Avatar");
							out.print("</a>");
						}
					%>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Gamer</h3>
						</div>
						<%
							if (acc.getNickname() != null) {
								out.print("<div class=\"panel-body\">" + acc.getNickname()
										+ "</div>");
							} else {
								out.print("<div class=\"panel-body\">" + acc.getUsername()
										+ "</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Full Name</h3>
						</div>
						<%
							String firstName = acc.getFirstName();
							String lastName = acc.getLastName();
							String fullName = "";
							if (firstName != null)
								fullName += firstName;
							if (lastName != null) {
								if (firstName != null)
									fullName += " ";
								fullName += lastName;
							}
							if (fullName.equals("")) {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							} else {
								out.print("<div class=\"panel-body\">" + fullName + "</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Birthdate</h3>
						</div>
						<%
							if (acc.getBirthdate() != null) {
								out.print("<div class=\"panel-body\">"
										+ acc.getBirthdate().toString() + "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Gender</h3>
						</div>
						<%
							if (acc.getGender() != null) {
								out.print("<div class=\"panel-body\">"
										+ acc.getGender().toUpperCase() + "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Country</h3>
						</div>
						<%
							if (acc.getCountry() != null) {
								out.print("<div class=\"panel-body\">" + acc.getCountry()
										+ "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Steam ID</h3>
						</div>
						<%
							if (acc.getSteamID() != null) {
								out.print("<div class=\"panel-body\">" + acc.getSteamID()
										+ "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">PlayStation Network ID:</h3>
						</div>
						<%
							if (acc.getPlayStationNetworkID() != null) {
								out.print("<div class=\"panel-body\">"
										+ acc.getPlayStationNetworkID() + "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Xbox Live Gamertag</h3>
						</div>
						<%
							if (acc.getXboxLiveGamertag() != null) {
								out.print("<div class=\"panel-body\">"
										+ acc.getXboxLiveGamertag() + "</div>");
							} else {
								out.print("<div class=\"panel-body\">Not Specified</div>");
							}
						%>
					</div>
				</div>
				<div class="row">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">E-mail</h3>
						</div>
						<div class="panel-body"><%=acc.getEmail()%></div>
					</div>
				</div>
				<div class="row text-center withmargins">
					<div class="btn-group-vertical btn-block">
						<%
							if (acc.getUsername().equals(loggedInUser)) {
								out.print("<a class=\"btn btn-info\" href=\"updateinfo.jsp\">Update Info");
								out.print("</a>");
								out.print("<a class=\"btn btn-warning\" href=\"changepassword.jsp\">Change Password");
								out.print("</a>");
							}
						%>
					</div>
				</div>
				<div class="row text-center withmargins">
					<%
						if (acc.getUsername().equals(loggedInUser)) {
							out.print("<a class=\"btn btn-danger btn-block text-center\" href=\"deleteaccount.jsp\">Delete My Account");
							out.print("</a>");
						}
					%>
				</div>
				<div class="row text-center withmargins">
					<%
						Account logged = manager.getAccount(loggedInUser);
						if (manager.isAdmin(logged)) {
							if (!manager.isBanned(acc)) {
								out.print("<form action=\"BanUser\" method=\"post\" role=\"form\">");
								out.print("<button type=\"submit\" class=\"btn btn-danger btn-block text-center\">Ban User</button>");
								out.print("<input type=\"hidden\" value=\""
										+ acc.getUsername() + "\" name=\"user\">");
								out.print("</form>");
							} else {
								out.print("<form action=\"UnbanUser\" method=\"post\" role=\"form\">");
								out.print("<button type=\"submit\" class=\"btn btn-success btn-block text-center\">Unban User</button>");
								out.print("<input type=\"hidden\" value=\""
										+ acc.getUsername() + "\" name=\"user\">");
								out.print("</form>");
							}
						}
					%>
				</div>
				<div class="row text-center withmargins">
					<%
						if (manager.isAdmin(logged)) {
							if (manager.isAdmin(acc)) {
								out.print("<form action=\"RemoveAdmin\" method=\"post\" role=\"form\">");
								out.print("<button type=\"submit\" class=\"btn btn-danger btn-block text-center\">Remove Admin</button>");
								out.print("<input type=\"hidden\" value=\""
										+ acc.getUsername() + "\" name=\"user\">");
								out.print("</form>");
							} else {
								out.print("<form action=\"AddAdmin\" method=\"post\" role=\"form\">");
								out.print("<button type=\"submit\" class=\"btn btn-success btn-block text-center\">Make Admin</button>");
								out.print("<input type=\"hidden\" value=\""
										+ acc.getUsername() + "\" name=\"user\">");
								out.print("</form>");
							}
						}
					%>
				</div>
			</div>
			<div class="col-md-9">
				<div class="row text-center withmargins">
					<div class="alert alert-success">Activity Content</div>
				</div>
				<%
					String content = request.getParameter("content");
					String ePageNumString = request.getParameter("epage");
					String rPageNumString = request.getParameter("rpage");
					int ePage, rPage;
					int limit = 13;
					if (ePageNumString != null)
						ePage = Integer.parseInt(ePageNumString);
					else
						ePage = 1;
					if (rPageNumString != null)
						rPage = Integer.parseInt(rPageNumString);
					else
						rPage = 1;
				%>
				<ul class="nav nav-tabs nav-justified">
					<%
						if (content == null || content.equals("entries")) {
							out.print("<li class=\"active\"><a href=\"#entries\" data-toggle=\"tab\">Entries</a>");
							out.print("</li>");
							out.print("<li><a href=\"#reviews\" data-toggle=\"tab\">Reviews</a></li>");
						} else if (content.equals("reviews")) {
							out.print("<li><a href=\"#entries\" data-toggle=\"tab\">Entries</a>");
							out.print("</li>");
							out.print("<li class=\"active\"><a href=\"#reviews\" data-toggle=\"tab\">Reviews</a></li>");
						}
					%>
				</ul>
				<div class="tab-content">
					<%
						if (content == null || content.equals("entries")) {
							out.print("<div id=\"entries\" class=\"tab-pane fade in active\">");
						} else {
							out.print("<div id=\"entries\" class=\"tab-pane fade\">");
						}
						List<Entry> entries = manager.getEntries(acc, ePage, limit);
						for (int i = 0; i < entries.size(); ++i) {
							Entry curr = entries.get(i);
							int entry_id = curr.getID();
							int game_id = curr.getGameID();
							String platform = curr.getPlatform();
							Timestamp subDate = curr.getSubmissionDate();
							GameplayTimes times = curr.getGameplayTimes();
							Game game = catalog.getGame(game_id);
							out.print("<div class=\"alert alert-success\">");
							out.print("<a href=\"entry.jsp?id=" + entry_id
									+ "\" class=\"alert-link\">");
							out.print("<span class=\"glyphicon glyphicon-ok-sign\"></span>");
							out.print(" " + game.getName() + ";");
							out.print(" On: " + platform + ";");
							out.print(" HOURS: <span class=\"label label-primary\">"
									+ times + "</span>");
							out.print(" SUBMITTED: " + subDate + ";");
							out.print("</a>");
							out.print("</div>");
						}
						out.print("<ul class=\"pager\">");
						int entriesSoFar = (ePage - 1) * limit + entries.size();
						int entriesNum = manager.getEntriesQuantity(acc);
						if (entriesNum > entries.size()) {
							if (ePage == 1) {
								out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
							} else {
								out.print("<li><a href=\"profile.jsp?username="
										+ acc.getUsername() + "&content=entries&epage=");
								out.print(ePage - 1);
								out.print("&rpage=" + rPage + "\">Previous</a></li>");
							}
							if (entriesSoFar <= limit) {
								out.print("<li><a href=\"profile.jsp?username="
										+ acc.getUsername() + "&content=entries&epage=");
								out.print(ePage + 1);
								out.print("&rpage=" + rPage + "\">Next</a></li>");
							} else {
								out.print("<li class=\"disabled\"><a href=\"profile.jsp?username="
										+ acc.getUsername() + "&content=entries&epage=");
								out.print(ePage + 1);
								out.print("&rpage=" + rPage + "\">Next</a></li>");
							}
						}
						out.print("</ul>");
						out.print("</div>");
					%>
					<%
						if (content == null || content.equals("entries")) {
							out.print("<div id=\"reviews\" class=\"tab-pane fade\">");
						} else {
							out.print("<div id=\"reviews\" class=\"tab-pane fade in active\">");
						}
						List<Review> reviews = manager.getReviews(acc, rPage, limit);
						for (int i = 0; i < reviews.size(); ++i) {
							Review curr = reviews.get(i);
							String author = curr.getAuthorUsername();
							int game_id = curr.getGameID();
							int rating = curr.getGivenRating();
							Timestamp revDate = curr.getReviewDate();
							Game game = catalog.getGame(game_id);

							if (rating >= 85)
								out.print("<div class=\"alert alert-success\">");
							else if (rating >= 60)
								out.print("<div class=\"alert alert-info\">");
							else if (rating >= 50)
								out.print("<div class=\"alert alert-warning\">");
							else
								out.print("<div class=\"alert alert-danger\">");
							out.print("<a href=\"review.jsp?author=" + author + "&game="
									+ game_id + "\" class=\"alert-link\">");
							out.print("<span class=\"glyphicon glyphicon-pencil\"></span>");
							out.print(" " + game.getName() + ";");
							out.print(" RATING: <span class=\"label label-primary\">"
									+ rating + "%</span>;");
							out.print(" SUBMITTED: " + revDate + ";");
							out.print("</a>");
							out.print("</div>");
						}
						out.print("<ul class=\"pager\">");
						int reviewsSoFar = (rPage - 1) * limit + reviews.size();
						int reviewsNum = manager.getReviewsQuantity(acc);
						if (reviewsNum > reviews.size()) {
							if (rPage == 1) {
								out.print("<li class=\"disabled\"><a href=\"#\">Previous</a></li>");
							} else {
								out.print("<li><a href=\"profile.jsp?username="
										+ acc.getUsername() + "&content=reviews&epage="
										+ ePage + "&rpage=");
								out.print(rPage - 1);
								out.print("\">Previous</a></li>");
							}
							if (reviewsSoFar <= limit) {
								out.print("<li><a href=\"profile.jsp?username="
										+ acc.getUsername() + "&content=reviews&epage="
										+ ePage + "&rpage=");
								out.print(rPage + 1);
								out.print("\">Next</a></li>");
							} else {
								out.print("<li class=\"disabled\"><a href=\"profile.jsp?username="
										+ acc.getUsername()
										+ "&content=reviews&epage="
										+ ePage + "&rpage=");
								out.print(rPage + 1);
								out.print("\">Next</a></li>");
							}
						}
						out.print("</ul>");
						out.print("</div>");
					%>
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