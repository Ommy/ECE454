package servers;

import ece454750s15a1.*;
import handlers.FPasswordHandler;
import handlers.ManagementHandler;

public class FEServer extends servers.Server {

    public static void main(String[] args) {
        FEServer server = new FEServer(args, ServerType.FE);
        server.run();
    }

    public FEServer(String[] args, ServerType type) {
        super(args, type);
    }

    public void run() {
        onStartupRegister();

        final A1Password.Iface pHandler = new FPasswordHandler();
        final A1Management.Iface mHandler = new ManagementHandler();

        final A1Password.Processor pProcessor = new A1Password.Processor<A1Password.Iface>(pHandler);
        final A1Management.Processor mProcessor = new A1Management.Processor<A1Management.Iface>(mHandler);

        onStartupInitializeServices(pProcessor, mProcessor);
    }



}

