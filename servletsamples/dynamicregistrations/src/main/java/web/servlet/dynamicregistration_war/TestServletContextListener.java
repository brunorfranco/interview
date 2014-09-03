package web.servlet.dynamicregistration_war;

import java.util.*;
import javax.servlet.*;

/**
 * @author Bruno
 */
public class TestServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext sc = sce.getServletContext();

        ServletRegistration sr = sc.addServlet("DynamicServlet",
            "web.servlet.dynamicregistration_war.TestServlet");
        sr.setInitParameter("servletInitName", "servletInitValue");
        sr.addMapping("/*");

        FilterRegistration fr = sc.addFilter("DynamicFilter",
            "web.servlet.dynamicregistration_war.TestFilter");
        fr.setInitParameter("filterInitName", "filterInitValue");
        fr.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST),
                                     true, "DynamicServlet");

        sc.addListener("web.servlet.dynamicregistration_war.TestServletRequestListener");
    }   

    @Override
    public void contextDestroyed(ServletContextEvent sce) {  
        // Do nothing
    }
}
