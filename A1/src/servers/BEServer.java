package servers;

import ece454750s15a1.*;
import handlers.BPasswordHandler;
import handlers.ManagementHandler;

public class BEServer extends BaseServer {

    public static void main(String[] args) {
        BEServer server = new BEServer(args, ServerType.BE);
        PerfCounters counter = server.getMyPerfCounter();
        server.run(new BPasswordHandler(server), new ManagementHandler(server, counter));
    }

    public BEServer(String[] args, ServerType type) {
        super(args, type);
    }
}
