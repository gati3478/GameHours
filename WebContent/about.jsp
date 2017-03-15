<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/about.css" rel="stylesheet">
<title>About</title>
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
					<li class="active"><a href="about.jsp">ABOUT</a></li>
				</ul>
				<%@include file="sign.jsp"%>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<div class="jumbotron">
		<div class="container">
			<h1 class="text-center">GameHours</h1>
			<p class="text-center">A website telling you how long it takes to
				beat the games</p>
			<img src="icons/gh_icon_256px.png"
				class="img-responsive img_center">
			<div class="text-center">
				<h1 class="label label-info">Contact US... Support US</h1>
				<a href="mailto:gpetr12@freeuni.edu.ge"
					class="btn btn-warning btn-large">gpetr12@freeuni.edu.ge</a> <a
					href="mailto:abodo12@freeuni.edu.ge"
					class="btn btn-warning btn-large">abodo12@freeuni.edu.ge</a> <a
					href="mailto:itkem12@freeuni.edu.ge"
					class="btn btn-warning btn-large">itkem12@freeuni.edu.ge</a>
				<h1 class="label label-info text-center">Contribute On GitHub</h1>
				<a href="https://github.com/gati3478/GameHours"><img
					src="icons/github-mark.png"></a>
				<h2>Click the GitHub icon, dummy</h2>
			</div>
			<small class="text-justify lower">Bootstrap - The MIT License
				(MIT) Copyright (c) 2011-2014 Twitter, Inc Permission is hereby
				granted, free of charge, to any person obtaining a copy of this
				software and associated documentation files (the "Software"), to
				deal in the Software without restriction, including without
				limitation the rights to use, copy, modify, merge, publish,
				distribute, sublicense, and/or sell copies of the Software, and to
				permit persons to whom the Software is furnished to do so, subject
				to the following conditions: The above copyright notice and this
				permission notice shall be included in all copies or substantial
				portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
				WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
				TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
				PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
				COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
				LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
				ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
				OR OTHER DEALINGS IN THE SOFTWARE.</small>
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