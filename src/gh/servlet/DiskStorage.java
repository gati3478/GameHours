package gh.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Servlet implementation class DiskStorage
 */
public class DiskStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static ServletContext sc;
	// private
	// "/WEB-INF/app.properties" also works...
	private static final String PROPERTIES_PATH = "/WEB-INF/app.properties";
	private Properties properties;

	@Override
	public void init() throws ServletException {
		super.init();
		// synchronize !
		if (sc == null)
			sc = getServletContext();
		try {
			loadProperties();
		} catch (IOException e) {
			throw new RuntimeException("Can't load properties file", e);
		}
	}

	/*
	 * Loads properties file.
	 */
	private void loadProperties() throws IOException {
		try (InputStream is = sc.getResourceAsStream(PROPERTIES_PATH)) {
			if (is == null)
				throw new RuntimeException("Can't locate properties file");
			properties = new Properties();
			properties.load(is);
		}
	}

	/*
	 * Returns property value by given property name.
	 */
	protected String property(final String key) {
		return properties.getProperty(key);
	}

}
