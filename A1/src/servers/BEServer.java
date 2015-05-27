package servers;

import ece454750s15a1.*;
import handlers.BPasswordHandler;
import handlers.ManagementHandler;

public class BEServer extends servers.Server {

    public static void main(String[] args) {
        BEServer server = new BEServer(args, ServerType.BE);
        server.run();
    }

    public BEServer(String[] args, ServerType type) {
        super(args, type);
    }

    public void run() {
        onStartupRegister();

        final A1Password.Iface pHandler = new BPasswordHandler();
        final A1Management.Iface mHandler = new ManagementHandler();

        final A1Password.Processor pProcessor = new A1Password.Processor<A1Password.Iface>(pHandler);
        final A1Management.Processor mProcessor = new A1Management.Processor<A1Management.Iface>(mHandler);

        onStartupInitializeServices(pProcessor, mProcessor);
    }
}
