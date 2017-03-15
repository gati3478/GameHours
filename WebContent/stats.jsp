<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="gh.datamodel.Account"%>
<%@page import="gh.db.managers.Statistics"%>
<%@page import="gh.datamodel.Game"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="html/header.html"%>
<link href="styles/stats.css" rel="stylesheet">
<title>Statistics</title>
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
					<li class="active"><a href="stats.jsp">STATS</a></li>
					<li><a href="about.jsp">ABOUT</a></li>
				</ul>
				<%@include file="sign.jsp"%>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<%
		Statistics stat = (Statistics) getServletContext().getAttribute(
				Statistics.ATTRIBUTE_NAME);
	%>
	<div class="jumbotron">
		<div class="container">
			<h2 class="text-center">
				<span class="label label-danger">Game Statistics Content</span>
			</h2>
	
			<%
				int limit = 5;

				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #E0FFFF\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-success\">TOP RATED GAMES</span></h3>");

				Map<Game, Integer> games = stat.getTopRatedGames(limit);
		        ValueComparatorA bvc =  new ValueComparatorA(games);
		        TreeMap<Game,Integer> sorted_map = new TreeMap<Game,Integer>(bvc);
		        sorted_map.putAll(games);
				for (Game game : sorted_map.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ game.getName()
							+ "</td><td style= \"color: #4169E1\">"
							+ games.get(game) + "</td>");

				}

				out.println("</table>");
				out.println("</div>");

				//most submitted

				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #E0FFFF\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-success\">MOST SUBMITTED</span></h3>");

				Map<Game, Integer> gamesA = stat.getMostSubmittedGames(limit);
		        ValueComparatorA bvcA =  new ValueComparatorA(gamesA);
		        TreeMap<Game,Integer> sorted_mapA = new TreeMap<Game,Integer>(bvcA);
		        sorted_mapA.putAll(gamesA);

		        for (Game game : sorted_mapA.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ game.getName()
							+ "</td><td style= \"color: #4169E1\">"
							+ gamesA.get(game) + "</td>");

				}

				out.println("</table>");
				out.println("</div>");

				//longest games

				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #E0FFFF\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-success\">LONGEST GAMES</span></h3>");

				Map<Game, Integer> gamesB = stat.getLongestGames(limit);
				ValueComparatorA bvcB =  new ValueComparatorA(gamesB);
		        TreeMap<Game,Integer> sorted_mapB = new TreeMap<Game,Integer>(bvcB);
		        sorted_mapB.putAll(gamesB);

		        for (Game game : sorted_mapB.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ game.getName()
							+ "</td><td style= \"color: #4169E1\">"
							+ gamesB.get(game) + "</td>");

				}

				out.println("</table>");
				out.println("</div>");

				//shortest games

				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #E0FFFF\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-success\">SHORTEST GAMES</span></h3>");

				Map<Game, Integer> gamesC = stat.getShortestGames(limit);
				ValueComparatorC bvcC =  new ValueComparatorC(gamesC);
		        TreeMap<Game,Integer> sorted_mapC = new TreeMap<Game,Integer>(bvcC);
		        sorted_mapC.putAll(gamesC);

		        for (Game game : sorted_mapC.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ game.getName()
							+ "</td><td style= \"color: #4169E1\">"
							+ gamesC.get(game) + "</td>");

				}

				out.println("</table>");
				out.println("</div>");
			%>
			
			<%
				//users by hours spent
				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #FFFACD\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-warning\">USERS BY HOURS SPENT</span></h3>");

				Map<Account, Integer> accountA = stat.getUsersByHoursSpent(limit);
				ValueComparatorB bvcD =  new ValueComparatorB(accountA);
		        TreeMap<Account,Integer> sorted_mapD = new TreeMap<Account, Integer>(bvcD);
		        sorted_mapD.putAll(accountA);

		        for (Account account : sorted_mapD.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ account.getNickname()
							+ "</td><td style= \"color: #4169E1\">"
							+ accountA.get(account) + "</td>");

				}


				// users by submission
				out.println("</table>");
				out.println("</div>");

				out.print("<div class=\"col-md-6\">");
				out.print("<table class=\"table table-bordered\" ");
				out.print(" style=\" background-color: #FFFACD\">");
				out.print(" <h3 class=\"text-center\"><span class=\"label label-warning\">USERS BY MOST SUBMISSIONS</span></h3>");

				Map<Account, Integer> accountB = stat.getUsersBySubmissions(limit);
				ValueComparatorB bvcE =  new ValueComparatorB(accountB);
		        TreeMap<Account,Integer> sorted_mapE = new TreeMap<Account, Integer>(bvcE);
		        sorted_mapE.putAll(accountB);

		        for (Account account : sorted_mapE.keySet()) {
					out.print("<tr><td style=\"width: 95%; color: #4169E1\"> "
							+ account.getNickname()
							+ "</td><td style= \"color: #4169E1\">"
							+ accountB.get(account) + "</td>");

				}

				out.println("</table>");
				out.println("</div>");
			%>
			

			<div class="row">
				<div class="col-md-6">
				<h3 class="text-center">
					<span class="label label-primary">PLATFORM STATISTICS</span>
				</h3>
				<p></p>
				<%
					int total = 0;
					Map<String, Integer> platform = stat.getPlatformStatistics();
					for (String string : platform.keySet())
						total += platform.get(string);
					for (String string : platform.keySet()) {
						out.println("<div class=\"progress\">");
						out.println("<div class=\"progress-bar progress-bar-success\" role=\"progressbar\"");
						out.println("aria-valuenow=\"" + platform.get(string)
								+ "\" aria-valuemin=\"0\" aria-valuemax=\" " + total
								+ "\"");
						out.println("style=\"width: "
								+ ((double) platform.get(string) / total) * 100
								+ "%\">");
						out.println("<div class=\"progressInnerLabel\" style=\"width: 600px; color : black\">"
								+ string + " - " + platform.get(string) + "</div>");

						out.println("<span class=\"sr-only\">"
								+ ((double) platform.get(string) / total) * 100
								+ "% Complete (success)</span>");
						out.println("</div>");
						out.println("</div>");
						
					}
				%>
			</div>
				<div class="col-md-6">
				<h3 class="text-center">
					<span class="label label-primary">USERS BY GENDER</span>
				</h3>
				<p></p>
				<%
					
					int female = stat.getFemaleQuantity();
					int male = stat.getMaleQuantity();
					
					
					//male
						out.println("<div class=\"progress\">");
						out.println("<div class=\"progress-bar progress-bar-danger\" role=\"progressbar\"");
						out.println("aria-valuenow=\"" + male
								+ "\" aria-valuemin=\"0\" aria-valuemax=\" " + female+male
								+ "\"");
						out.println("style=\"width: "
								+ ((double)male / (male+female)) * 100
								+ "%\">");
						out.println("<div class=\"progressInnerLabel\" style=\"width: 600px; color : black\">"
								+ "Male" + " - " + male + "</div>");

						out.println("<span class=\"sr-only\">"
								+ ((double)male / (male+female)) * 100
								+ "% Complete </span>");
						out.println("</div>");
						out.println("</div>");
						
					
						
						//female
						out.println("<div class=\"progress\">");
						out.println("<div class=\"progress-bar progress-bar-warning\" role=\"progressbar\"");
						out.println("aria-valuenow=\"" + female
								+ "\" aria-valuemin=\"0\" aria-valuemax=\" " + female+male
								+ "\"");
						out.println("style=\"width: "
								+ ((double)female / (male+female)) * 100
								+ "%\">");
						out.println("<div class=\"progressInnerLabel\" style=\"width: 600px; color : black\">"
								+ "Female" + " - " + female + "</div>");

						out.println("<span class=\"sr-only\">"
								+ ((double)female / (male+female)) * 100
								+ "% Complete </span>");
						out.println("</div>");
						out.println("</div>");
				%>
			</div>
		</div>
		</div>
	</div>




	<%!
	class ValueComparatorA implements Comparator<Game> {

	    Map<Game, Integer> base;
	    public ValueComparatorA(Map<Game, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(Game a, Game b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	
	class ValueComparatorC implements Comparator<Game> {

	    Map<Game, Integer> base;
	    public ValueComparatorC(Map<Game, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(Game a, Game b) {
	        if (base.get(a) >= base.get(b)) {
	            return 1;
	        } else {
	            return -1;
	        } // returning 0 would merge keys
	    }
	}
	
	class ValueComparatorB implements Comparator<Account> {

	    Map<Account, Integer> base;
	    public ValueComparatorB(Map<Account, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(Account a, Account b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	%>
	<div id="footer">
		<div class="container">
			<p class="text-muted"><%@include file="html/footer.html"%>
			</p>
		</div>
	</div>
	<!-- /container -->
	<%@include file="html/include_scripts.html"%>
</html>