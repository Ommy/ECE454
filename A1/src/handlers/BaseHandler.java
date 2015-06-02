package handlers;

import ece454750s15a1.PerfCounters;
import servers.IServer;
import java.util.Calendar;


public abstract class BaseHandler {
    protected final IServer myServer;
    protected PerfCounters counter;
    protected final long serverStartTime;

    protected BaseHandler(IServer server) {
        this.myServer = server;

        serverStartTime = Calendar.getInstance().getTimeInMillis();
        counter = myServer.getPerfCounters();
    }

    protected synchronized void updateRequestsReceived() {
        int received = counter.getNumRequestsReceived();
        counter.setNumRequestsReceived(received + 1);
    }

    protected synchronized void updateRequestsCompleted() {
        int completed = counter.getNumRequestsCompleted();
        counter.setNumRequestsCompleted(completed + 1);
    }
}
