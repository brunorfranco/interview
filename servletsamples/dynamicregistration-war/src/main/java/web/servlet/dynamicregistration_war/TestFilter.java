package web.servlet.dynamicregistration_war;

import java.io.*;
import javax.servlet.*;

/**
 * @author Bruno
 */
public class TestFilter implements Filter {

    private String filterInitParam;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filterInitParam = filterConfig.getInitParameter("filterInitName");
    }   

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        req.setAttribute("filterInitName", filterInitParam);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        // Do nothing
    }
}
