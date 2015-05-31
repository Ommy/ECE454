package handlers;

import servers.IServer;
import services.ServiceExecutor;
import services.RandomScheduler;

public abstract class BaseHandler {
    protected final IServer server;

    protected BaseHandler(IServer server) {
        this.server = server;
    }
}
