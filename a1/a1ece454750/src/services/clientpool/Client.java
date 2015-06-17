package services.clientpool;


import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;

import java.io.Closeable;

public class Client<T extends TServiceClient> implements Closeable {
    public static class Factory {
        private static A1Management.Client.Factory managementFactory = new A1Management.Client.Factory();
        private static A1Password.Client.Factory passwordFactory = new A1Password.Client.Factory();
        private static TCompactProtocol.Factory compactProtocolFactory = new TCompactProtocol.Factory();
        private static TBinaryProtocol.Factory binaryProtocolFactory = new TBinaryProtocol.Factory();

        public static Client<A1Management.Client> createSimpleManagementClient(String host, int mport) {
            TFramedTransport transport = new TFramedTransport(new TSocket(host, mport));
            TProtocol protocol = compactProtocolFactory.getProtocol(transport); //binaryProtocolFactory.getProtocol(transport);
            A1Management.Client client = managementFactory.getClient(protocol);

            return new Client<A1Management.Client>(transport, protocol, client);
        }

        public static Client<A1Password.Client> createSimplePasswordClient(String host, int pport) {
            TTransport transport = new TFramedTransport(new TSocket(host, pport));
            TProtocol protocol = compactProtocolFactory.getProtocol(transport); //binaryProtocolFactory.getProtocol(transport);
            A1Password.Client client = passwordFactory.getClient(protocol);

            return new Client<A1Password.Client>(transport, protocol, client);
        }

        public static <T extends TServiceClient> Client<T> createSimpleClient(String host, int port, TServiceClientFactory<T> factory) {
            TTransport transport = new TFramedTransport(new TSocket(host, port));
            TProtocol protocol = compactProtocolFactory.getProtocol(transport); //binaryProtocolFactory.getProtocol(transport);
            T client = factory.getClient(protocol);

            return new Client<T>(transport, protocol, client);
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

    public T getClient() {
        return client;
    }

    public void open() throws TException {
        if ((transport != null) && !transport.isOpen()) {
            transport.open();
        }
    }

    public void close() {
        if ((transport != null) && transport.isOpen()) {
            transport.close();
        }
    }
}
