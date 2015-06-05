package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessorFactory;
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
    private static TFramedTransport.Factory framedTransportFactory = new TFramedTransport.Factory();
    private static TCompactProtocol.Factory compactProtocolFactory = new TCompactProtocol.Factory();

    public void serveManagementEndpoint(ServerDescription description, final A1Management.Iface handler) {
        LOGGER.info("Serving management endpoint");

        A1Management.Processor processor = new A1Management.Processor<A1Management.Iface>(handler);
        try {
            // TODO: Choose an appropriate transport and protocol

            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getMport());
            TThreadedSelectorServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(transport)
                    .processor(processor)
                    .transportFactory(framedTransportFactory)
                    .protocolFactory(compactProtocolFactory)
                    .selectorThreads(1)
                    .workerThreads(2)
            );

            LOGGER.info("Can serve management endpoint");

            server.serve();
        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Management endpoint error: ", te);
        }

        LOGGER.info("Stopped serving management endpoint");
    }

    public void serveFEPasswordEndpoint(ServerDescription description, final A1Password.Iface handler) {
        LOGGER.info("Serving password endpoint");

        A1Password.Processor processor = new A1Password.Processor<A1Password.Iface>(handler);
        try {
            TServerSocket transport = new TServerSocket(description.getPport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                    .processor(processor)
                    .protocolFactory(compactProtocolFactory)
                    .maxWorkerThreads(5)
            );

            LOGGER.info("Can serve password endpoint");

            server.serve();
        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Password endpoint error: ", te);
        }

        LOGGER.info("Stopped serving password endpoint");
    }

    public void serveBEPasswordEndpoint(ServerDescription description, final A1Password.Iface handler) {
        LOGGER.info("Serving password endpoint");

        A1Password.Processor processor = new A1Password.Processor<A1Password.Iface>(handler);
        try {
            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getPport());
            TThreadedSelectorServer server = new TThreadedSelectorServer(new TThreadedSelectorServer.Args(transport)
                    .processor(processor)
                    .transportFactory(framedTransportFactory)
                    .protocolFactory(compactProtocolFactory)
                    .selectorThreads(1)
                    .workerThreads(2)
            );

            LOGGER.info("Can serve password endpoint");
            server.serve();
        } catch (TException te) {
            // TODO: Handle exception
            LOGGER.error("Password endpoint error: ", te);
        }

        LOGGER.info("Stopped serving password endpoint");
    }

}
