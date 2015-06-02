package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointProvider.class.getName());

    public void serveManagementEndpoint(ServerDescription description, final A1Management.Iface handler) {
        LOGGER.debug("Serving management endpoint");

        A1Management.Processor processor = new A1Management.Processor<A1Management.Iface>(handler);
        try {
            // TODO: Choose an appropriate transport and protocol
            TServerSocket transport = new TServerSocket(description.getMport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                    .processor(processor)
                    .maxWorkerThreads(Integer.MAX_VALUE)
                    .minWorkerThreads(description.getNcores()));
            server.serve();

        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Management endpoint error: ", te);
        }

        LOGGER.debug("Stopped serving management endpoint");
    }

    public void servePasswordEndpoint(ServerDescription description, final A1Password.Iface handler) {
        LOGGER.debug("Serving password endpoint");

        A1Password.Processor processor = new A1Password.Processor<A1Password.Iface>(handler);
        try {
            TServerSocket transport = new TServerSocket(description.getPport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                    .processor(processor)
                    .maxWorkerThreads(Integer.MAX_VALUE)
                    .minWorkerThreads(description.getNcores()));
            server.serve();
        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Password endpoint error: ", te);
        }

        LOGGER.debug("Stopped serving password endpoint");
    }

    public void serveManagementEndpointAsync(ServerDescription description, final A1Management.AsyncIface handler) {
        A1Management.AsyncProcessor processor = new A1Management.AsyncProcessor<A1Management.AsyncIface>(handler);
    }

    public void servePassswordEndpointAsync(ServerDescription description, final A1Password.AsyncIface handler) {
        A1Password.AsyncProcessor processor = new A1Password.AsyncProcessor<A1Password.AsyncIface>(handler);
    }
}
