package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointProvider.class.getName());

    public void serveManagementEndpoint(ServerDescription description, final A1Management.Iface handler) {
        LOGGER.info("Serving management endpoint");

        A1Management.Processor processor = new A1Management.Processor<A1Management.Iface>(handler);
        try {
            // TODO: Choose an appropriate transport and protocol
            TServerSocket transport = new TServerSocket(description.getMport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                    .protocolFactory(new TCompactProtocol.Factory())
                    .processor(processor)
                    .maxWorkerThreads(5));

            LOGGER.info("Can serve management endpoint");

            server.serve();

        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Management endpoint error: ", te);
        }

        LOGGER.info("Stopped serving management endpoint");
    }

    public void servePasswordEndpoint(ServerDescription description, final A1Password.Iface handler) {
        LOGGER.info("Serving password endpoint");

        A1Password.Processor processor = new A1Password.Processor<A1Password.Iface>(handler);
        try {

            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getPport());
            TThreadedSelectorServer server = new TThreadedSelectorServer(
                    new TThreadedSelectorServer.Args(transport).processor(processor)
                            .transportFactory(new TFramedTransport.Factory())
                            .protocolFactory(new TCompactProtocol.Factory())
                            .selectorThreads(8)
                            .workerThreads(8));

            LOGGER.info("Can serve password endpoint");

            server.serve();
        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Password endpoint error: ", te);
        }

        LOGGER.info("Stopped serving password endpoint");
    }


    public void serveManagementEndpointAsync(ServerDescription description, final A1Management.AsyncIface handler) {

        try {
            A1Management.AsyncProcessor processor = new A1Management.AsyncProcessor<A1Management.AsyncIface>(handler);
            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getPport());
            TServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(transport).processor(processor).workerThreads(10).selectorThreads(10));
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public void servePassswordEndpointAsync(ServerDescription description, final A1Password.AsyncIface handler) {

        try {
            A1Password.AsyncProcessor processor = new A1Password.AsyncProcessor<A1Password.AsyncIface>(handler);
            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getMport());
            TServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(transport).processor(processor).workerThreads(10).selectorThreads(10));
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
