package web.servlet.dynamicregistration_war;

import javax.servlet.*;

/**
 * @author Bruno
 */

public class TestServletRequestListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent sre) {
        sre.getServletRequest().setAttribute("listenerAttributeName",
            "listenerAttributeValue");
    }
    
    public void requestDestroyed(ServletRequestEvent sre) {
        // Do nothing
    }

}
