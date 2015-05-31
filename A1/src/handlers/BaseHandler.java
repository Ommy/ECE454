package handlers;

import servers.IServer;

public abstract class BaseHandler {
    protected final IServer server;

    protected BaseHandler(IServer server) {
        this.server = server;
    }
}
