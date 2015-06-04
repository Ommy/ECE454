package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.Closeable;
import java.io.IOException;

public class AsyncClient<T extends TAsyncClient> implements Closeable {
    public static class Factory {
        private static A1Management.AsyncClient.Factory managementFactory = null;
        private static A1Password.AsyncClient.Factory passwordFactory = null;

        private static void initialize() throws IOException {
            if (managementFactory == null) {
                TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
                TAsyncClientManager clientManager = new TAsyncClientManager();
                managementFactory = new A1Management.AsyncClient.Factory(clientManager, protocolFactory);
            }

            if (passwordFactory == null) {
                TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
                TAsyncClientManager clientManager = new TAsyncClientManager();
                passwordFactory = new A1Password.AsyncClient.Factory(clientManager, protocolFactory);
            }
        }

        public static AsyncClient<A1Management.AsyncClient> createSimpleManagementClient(String host, int mport) throws IOException {
            initialize();
            return createSimpleClient(host, mport, managementFactory);
        }

        public static AsyncClient<A1Password.AsyncClient> createSimplePasswordClient(String host, int mport) throws IOException {
            initialize();
            return createSimpleClient(host, mport, passwordFactory);
        }

        public static AsyncClient<A1Management.AsyncClient> createSimpleManagementClient(ServerDescription server) throws IOException {
            return createSimpleManagementClient(server.getHost(), server.getMport());
        }

        public static AsyncClient<A1Password.AsyncClient> createSimplePasswordClient(ServerDescription server) throws IOException {
            return createSimplePasswordClient(server.getHost(), server.getPport());
        }

        public static <T extends TAsyncClient> AsyncClient<T> createSimpleClient(String host, int port, TAsyncClientFactory<T> factory) throws IOException {
            TNonblockingTransport transport = new TNonblockingSocket(host, port);
            AsyncClient<T> client = new AsyncClient<T>(transport, factory.getAsyncClient(transport));
            return client;
        }
    }

    private final TTransport transport;
    private final T client;

    public AsyncClient(TTransport transport, T client) {
        this.transport = transport;
        this.client = client;
    }

    public void open() throws TTransportException {
        if (!transport.isOpen()) {
            transport.open();
        }
    }

    public T getClient() {
        return client;
    }

    @Override
    public void close() {
        if (transport.isOpen()) {
            transport.close();
        }
    }
}
