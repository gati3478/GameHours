<%@page import="gh.db.managers.AccountManager"%>
<%@page import="gh.datamodel.Account"%>
<ul class="nav navbar-nav navbar-right">
	<%
		String username = (String) session.getAttribute("username");
		if (username != null) {
			AccountManager manager = (AccountManager) getServletContext()
					.getAttribute(AccountManager.ATTRIBUTE_NAME);
			Account acc = manager.getAccount(username);
			out.print("<li>");
			out.print("<a href=\"profile.jsp?username=" + username + "\">");
			out.print("<img src=\"GetImage?image=avatars/thumbnails_44x44/");
			if (acc.getAvatarFilename() == null)
				out.print("default.jpg");
			else
				out.print(acc.getAvatarFilename());
			out.print("\" class=\"img-rounded avatarthumb\">");
			out.print("</a>");
			out.print("</li>");
			out.print("<li>");
			out.print("<a href=\"profile.jsp?username=" + username + "\">");
			if (acc.getNickname() == null)
				out.print(username);
			else
				out.print(acc.getNickname());
			out.print("</a>");
			out.print("</li>");
			if (manager.isAdmin(acc)) {
				out.print("<li>");
				out.print("<form action=\"admin.jsp\" class=\"navbar-form\" role=\"form\">");
				out.print("<button type=\"submit\" class=\"btn btn-success\">Admin</button>");
				out.print("</form>");
				out.print("</li>");
			}
			out.print("<li>");
			out.print("<form action=\"SignOut\" class=\"navbar-form\" role=\"form\">");
			out.print("<button type=\"submit\" id=\"disconnect\" class=\"btn btn-info\">Sign Out</button>");
			out.print("</form>");
			out.print("</li>");
		} else {
			out.print("<a href=\"signup.jsp\" class=\"navbar-form navbar-right\">");
			out.print("<button type=\"submit\" class=\"btn btn-info\">Sign Up</button>");
			out.print("</a>");
			out.print("<a href=\"signin.jsp\" class=\"navbar-form navbar-right\">");
			out.print("<button type=\"submit\" class=\"btn btn-success\">Sign In</button>");
			out.print("</a>");
		}
	%>
</ul>