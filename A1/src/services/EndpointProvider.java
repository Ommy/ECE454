package services;

import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;

public class EndpointProvider {
    public void serveManagementEndpoint(ServerDescription description, TProcessor processor) {
        System.out.println("Serving management endpoint");
        try {
            // TODO: Choose an appropriate transport and protocol
            TServerSocket transport = new TServerSocket(description.getMport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor).maxWorkerThreads(10).minWorkerThreads(2));
            server.serve();

        } catch (TException te) {
            // TODO: Handle exception
            System.out.println("Management endpoint error");
            te.printStackTrace();
        }

        System.out.println("Stopped serving management endpoint");
    }

    public void servePasswordEndpoint(ServerDescription description, TProcessor processor) {
        System.out.println("Serving password endpoint");

        try {
            TServerSocket transport = new TServerSocket(description.getPport());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor));
            server.serve();

        } catch (TException te) {
            // TODO: Handle exception
            System.out.println("Password endpoint error");

            te.printStackTrace();
        }

        System.out.println("Stopped serving password endpoint");
    }
}
