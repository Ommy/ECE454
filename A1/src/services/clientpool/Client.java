package services.clientpool;


import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.Closeable;
import java.io.IOException;

public class Client<T extends TServiceClient> implements Closeable {
    public static class Factory {
        private static A1Management.Client.Factory managementFactory = new A1Management.Client.Factory();
        private static A1Password.Client.Factory passwordFactory = new A1Password.Client.Factory();

        public static Client<A1Management.Client> createSimpleManagementClient(String host, int mport) {
            return createSimpleClient(host, mport, managementFactory);
        }

        public static Client<A1Password.Client> createSimplePasswordClient(String host, int mport) {
            return createSimpleClient(host, mport, passwordFactory);
        }

        public static Client<A1Management.Client> createSimpleManagementClient(ServerDescription server) {
            return createSimpleClient(server.getHost(), server.getMport(), managementFactory);
        }

        public static Client<A1Password.Client> createSimplePasswordClient(ServerDescription server) {
            return createSimpleClient(server.getHost(), server.getPport(), passwordFactory);
        }

        public static <T extends TServiceClient> Client<T> createSimpleClient(String host, int port, TServiceClientFactory<T> factory) {
            TTransport transport = new TSocket(host, port);
            TProtocol protocol = new TBinaryProtocol(transport);
            T client = factory.getClient(protocol);

            Client<T> continuousClient = new Client<T>(transport, protocol, client);

            return continuousClient;
        }
    }

    private final TTransport transport;
    private final TProtocol protocol;
    private final T client;

    public Client(TTransport transport, TProtocol protocol, T client) {
        this.transport = transport;
        this.protocol = protocol;
        this.client = client;
    }

    public TTransport getTransport() {
        return transport;
    }

    public TProtocol getProtocol() {
        return protocol;
    }

    public T getClient() {
        return client;
    }

    public void open() throws TException {
        if (!transport.isOpen()) {
            transport.open();
        }
    }

    @Override
    public void close() throws IOException {
        if (transport.isOpen()) {
            transport.close();
        }
    }
}
