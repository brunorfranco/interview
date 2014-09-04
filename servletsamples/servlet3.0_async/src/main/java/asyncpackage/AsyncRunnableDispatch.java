package asyncpackage;

import javax.servlet.AsyncContext;

public class AsyncRunnableDispatch implements Runnable {

    AsyncContext ctx;

    public AsyncRunnableDispatch(AsyncContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        try {
            Thread.sleep(10000);
        } catch (Exception ie) {
        }

        ctx.dispatch("/response.jsp");
    }
}
