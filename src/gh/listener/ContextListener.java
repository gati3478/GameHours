package gh.listener;

import gh.db.DBInfo;
import gh.db.managers.AccountManager;
import gh.db.managers.GameCatalog;
import gh.db.managers.Statistics;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * Application Lifecycle Listener implementation class ContextListener
 *
 */
@WebListener
public class ContextListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource dataSource = (DataSource) envContext.lookup("jdbc/"
					+ DBInfo.DB_NAME);
			arg0.getServletContext().setAttribute("DataSource", dataSource);
			arg0.getServletContext().setAttribute(GameCatalog.ATTRIBUTE_NAME,
					new GameCatalog(dataSource));
			arg0.getServletContext().setAttribute(
					AccountManager.ATTRIBUTE_NAME,
					new AccountManager(dataSource));
			arg0.getServletContext().setAttribute(Statistics.ATTRIBUTE_NAME,
					new Statistics(dataSource));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
