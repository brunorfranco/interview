package web.servlet.annotation_war;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

/**
 * This class illustrates WebListener annotation.
 *
 * @author Bruno
 */
@WebListener
public class TestServletContextListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.setAttribute("listenerMessage", "my listener");
    }   

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
