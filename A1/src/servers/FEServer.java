package servers;

import ece454750s15a1.*;
import handlers.BPasswordHandler;
import handlers.FPasswordHandler;
import handlers.ManagementHandler;

public class FEServer extends BaseServer {

    public static void main(String[] args) {
        FEServer server = new FEServer(args, ServerType.FE);
        server.run(new FPasswordHandler(server), new ManagementHandler(server));
    }

    public FEServer(String[] args, ServerType type) {
        super(args, type);
    }
}

