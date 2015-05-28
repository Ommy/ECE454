package handlers;

import servers.IServer;
import services.ServiceExecutor;
import services.SimpleScheduler;

public abstract class BaseHandler {
    private final IServer server;
    protected final ServiceExecutor executor;

    protected BaseHandler(IServer server) {
        this.server = server;

        executor = new ServiceExecutor(server, new SimpleScheduler(server));
    }

}
