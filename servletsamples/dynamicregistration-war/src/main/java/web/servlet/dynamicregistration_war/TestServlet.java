package web.servlet.dynamicregistration_war;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Bruno
 */

public class TestServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8368830348254700427L;

	@Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        if (!"servletInitValue".equals(getServletConfig().getInitParameter(
                "servletInitName"))) {
            throw new ServletException("Missing servlet init param");
        }

        if (!"filterInitValue".equals(req.getAttribute("filterInitName"))) {
            throw new ServletException("Missing request attribute that was "
                    + "supposed to have been set by programmtically registered "
                    + "Filter");
        }

        if (!"listenerAttributeValue".equals(req.getAttribute(
                "listenerAttributeName"))) {
            throw new ServletException("Missing request attribute that was "
                    + "supposed to have been set by programmtically registered "
                    + "ServletRequestListener");
        }

        res.getWriter().println("HELLO WORLD!\n");
    }
}
