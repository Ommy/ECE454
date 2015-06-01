package handlers;

import ece454750s15a1.PerfCounters;
import servers.IServer;
import java.util.Calendar;


public abstract class BaseHandler {
    protected final IServer server;
    protected PerfCounters counter;
    protected final long serverStartTime;

    protected BaseHandler(IServer server) {
        this.server = server;

        serverStartTime = Calendar.getInstance().getTimeInMillis();
        counter = this.server.getPerfCounters();
    }

    protected synchronized void updateRequestsReceived() {
        counter.setNumRequestsReceived(counter.getNumRequestsReceived() + 1);
    }

    protected synchronized void updateRequestsCompleted() {
        counter.setNumRequestsCompleted(counter.getNumRequestsCompleted() + 1);
    }
}
