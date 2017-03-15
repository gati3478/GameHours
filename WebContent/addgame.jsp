<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="gh.db.managers.GameCatalog"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/signup.css" rel="stylesheet">
<link href="styles/jumbotron-narrow_addgame.css" rel="stylesheet">
<title>Add New Game</title>
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
		GameCatalog catalog = (GameCatalog) getServletContext()
				.getAttribute(GameCatalog.ATTRIBUTE_NAME);
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
	<div class="container not_nav">
		<div class="jumbotron not_nav">
			<h1 class="text-center">
				<span class="label label-primary">Add New Game Here!</span>
			</h1>
			<form action="AddGame" class="form-horizontal" method="post"
				enctype="multipart/form-data" role="form">
				<div class="form-group">
					<label for="inputName3" class="col-sm-2 control-label">Game:</label>
					<div class="col-sm-10">
						<input type="text" class="form-control"
							placeholder="Name of the game goes here..." required autofocus
							id="game" name="game">
					</div>
				</div>
				<div class="form-group">
					<label for="inputName3" class="col-sm-2 control-label">Developers:</label>
					<div class="col-sm-10">
						<input type="text" class="form-control"
							placeholder="Developer names goes here..." autofocus
							id="developers" name="developers">
					</div>
				</div>
				<div class="form-group">
					<label for="inputName3" class="col-sm-2 control-label">Publishers:</label>
					<div class="col-sm-10">
						<input type="text" class="form-control"
							placeholder="Name of the publishers goes here..." autofocus
							id="publishers" name="publishers">
					</div>
				</div>
				<div class="form-group">
					<label for="inputName3" class="col-sm-2 control-label">Platforms:</label>
					<div class="col-sm-10">
						<%@include file="html/platforms.html"%>
					</div>
				</div>
				<div class="form-group">
					<label for="inputName3" class="col-sm-2 control-label">Genres:</label>
					<div class="col-sm-10">
						<%@include file="html/genres.html"%>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Release Date:</label>
					<div class="col-sm-10">
						<input type="date" class="form-control input-sm"
							placeholder="01/01/1994  (Optional)" id="releaseDate"
							name="releaseDate">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">Short Description:</label>
					<div class="col-sm-10">
						<textarea id="shortDescription" class="form-control" rows="4"
							placeholder="Short description goes here..." required></textarea>
						<input id="descriptionInput" type="hidden"
							class="form-control textarea"
							placeholder="Short description goes here..." required
							name="shortDescription">
					</div>
				</div>
				<div class="input-group">
					<span class="input-group-btn"> <span
						class="btn btn-primary btn-file"> Browse Cover
							Image&hellip; <input type="file" name="file" />
					</span>
					</span> <input type="text" class="form-control" readonly>
				</div>
				<button class="btn btn-lg btn-success submit_button addgamebtn"
					type="submit" value="Upload">ADD GAME</button>
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
	<script>
		$(document).on(
				'change',
				'.btn-file :file',
				function() {
					var input = $(this), numFiles = input.get(0).files ? input
							.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [ numFiles, label ]);
				});

		$(document)
				.ready(
						function() {
							$('.btn-file :file')
									.on(
											'fileselect',
											function(event, numFiles, label) {

												var input = $(this).parents(
														'.input-group').find(
														':text'), log = numFiles > 1 ? numFiles
														+ ' files selected'
														: label;

												if (input.length) {
													input.val(log);
												} else {
													if (log)
														alert(log);
												}

											});
						});
		$(document).ready(function() {
			$("#shortDescription").change(function() {
				$("#descriptionInput").val($("#shortDescription").val());
			});
		});
	</script>
</body>
</html>