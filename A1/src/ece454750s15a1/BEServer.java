package ece454750s15a1;

import handlers.BPasswordHandler;
import handlers.ManagementHandler;
import servers.BaseServer;

public class BEServer extends BaseServer {

    public static void main(String[] args) {
        BEServer server = new BEServer(args, ServerType.BE);
        server.run(new BPasswordHandler(server), new ManagementHandler(server));
    }

    public BEServer(String[] args, ServerType type) {
        super(args, type);
    }
}
