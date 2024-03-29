package ece454750s15a1;

import handlers.FPasswordHandler;
import handlers.ManagementHandler;
import servers.BaseServer;

public class FEServer extends BaseServer {

    public static void main(String[] args) {
        FEServer server = new FEServer(args, ServerType.FE);
        server.run(new FPasswordHandler(server), new ManagementHandler(server));
    }

    public FEServer(String[] args, ServerType type) {
        super(args, type);
    }
}

