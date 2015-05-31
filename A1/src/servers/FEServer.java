package servers;

import ece454750s15a1.*;
import handlers.FPasswordHandler;
import handlers.ManagementHandler;

public class FEServer extends BaseServer {

    public static void main(String[] args) {
        FEServer server = new FEServer(args, ServerType.FE);
        PerfCounters counter = server.getMyPerfCounter();
        server.run(new FPasswordHandler(server, counter), new ManagementHandler(server, counter));
    }

    public FEServer(String[] args, ServerType type) {
        super(args, type);
    }
}

