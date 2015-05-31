package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;
import servers.IServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

public class ClientPoolService implements Closeable, IClientService {
    public final IServer myServer;

    private HashMap<String, ServerDescription> servers;
    private HashMap<String, Client<A1Management.Client>> managementClients;
    private HashMap<String, Client<A1Password.Client>> passwordClients;
    private HashMap<String, AsyncClient<A1Management.AsyncClient>> managementAsyncClients;
    private HashMap<String, AsyncClient<A1Password.AsyncClient>> passwordAsyncClients;

    public ClientPoolService(IServer server) {
        this.myServer = server;

        servers = new HashMap<String, ServerDescription>();

        managementClients = new HashMap<String, Client<A1Management.Client>>();
        passwordClients = new HashMap<String, Client<A1Password.Client>>();

        managementAsyncClients = new HashMap<String, AsyncClient<A1Management.AsyncClient>>();
        passwordAsyncClients = new HashMap<String, AsyncClient<A1Password.AsyncClient>>();
    }

    private String hash(ServerDescription server) {
        return server.getHost() + "," + server.getPport() + "," + server.getMport();
    }

    @Override
    public <T> T callOnce(String host, int mport, IManagementServiceRequest request) {

        T result = null;
        try {
            System.out.println("Calling with one time client connection");

            Client<A1Management.Client> client = Client.Factory.createSimpleManagementClient(host, mport);
            client.open();
            result = request.perform(client.getClient());
            client.close();

            System.out.println("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            System.out.println("callOnce failed 1");
            te.printStackTrace();
        } catch (IOException e) {
            // TODO: Handle errors
            System.out.println("callOnce failed 2");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public <T> T call(ServerDescription targetServer, IManagementServiceRequest request) {
        System.out.println("ClientPoolService call - IManagementService");

        T result = null;
        try {
            System.out.println("Re-using client connection");

            Client<A1Management.Client> client = getManagementClient(targetServer);

            client.open();

            result = request.perform(client.getClient());

            System.out.println("Completed re-usable client connection");
        } catch (TException te) {
            System.out.println("Failed to re-use client connection: TException");

            handleClientFailed(targetServer);
            myServer.onConnectionFailed(targetServer);

            te.printStackTrace();
        }

        return result;
    }

    @Override
    public <T> T call(ServerDescription targetServer, IPasswordServiceRequest request) {
        System.out.println("ClientPoolService call - IPasswordService");

        T value = null;
        try {
            System.out.println("Re-using client connection");

            Client<A1Password.Client> client = getPasswordClient(targetServer);
            System.out.println("Got client connection");

            client.open();

            value = request.perform(client.getClient());

            System.out.println("Completed re-usable client connection");
        } catch (TException te) {
            System.out.println("Failed to re-use client connection");

            handleClientFailed(targetServer);
            myServer.onConnectionFailed(targetServer);

            te.printStackTrace();
        }

        return value;
    }

    @Override
    public <T> T callOnceAsync(String host, int mport, IManagementServiceAsyncRequest request) {
        T result = null;
        try {
            System.out.println("Calling with one time client connection");

            AsyncClient<A1Management.AsyncClient> client = AsyncClient.Factory.createManagementClient(host, mport);

            if (client != null) {
                client.open();
                result = request.perform(client.getClient());
                client.close();
            }

            System.out.println("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            System.out.println("callOnce failed 1");
            te.printStackTrace();
        } catch (IOException e) {
            // TODO: Handle errors
            System.out.println("callOnce failed 2");
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public <T> T callAsync(ServerDescription targetServer, IManagementServiceAsyncRequest request) {
        System.out.println("ClientPoolService call - IManagementService");

        T result = null;
        try {
            System.out.println("Re-using client connection");

            AsyncClient<A1Management.AsyncClient> client = getManagementAsyncClient(targetServer);

            if (client != null) {
                client.open();
                result = request.perform(client.getClient());
            }

            System.out.println("Completed re-usable client connection");
        } catch (TException te) {
            System.out.println("Failed to re-use client connection: TException");

            handleClientFailed(targetServer);
            myServer.onConnectionFailed(targetServer);

            te.printStackTrace();
        }

        return result;
    }

    @Override
    public <T> T callAsync(ServerDescription targetServer, IPasswordServiceAsyncRequest request) {
        System.out.println("ClientPoolService call - IPasswordService");

        T result = null;
        try {
            System.out.println("Re-using client connection");

            AsyncClient<A1Password.AsyncClient> client = getPasswordAsyncClient(targetServer);

            if (client != null) {
                client.open();
                result = request.perform(client.getClient());
            }

            System.out.println("Completed re-usable client connection");
        } catch (TException te) {
            System.out.println("Failed to re-use client connection: TException");

            handleClientFailed(targetServer);
            myServer.onConnectionFailed(targetServer);

            te.printStackTrace();
        }

        return result;
    }

    private Client<A1Management.Client> getManagementClient(ServerDescription server) {
        Client<A1Management.Client> client = null;
        if (!managementClients.containsKey(hash(server))) {
            client = Client.Factory.createSimpleManagementClient(server);
            managementClients.put(hash(server), client);
            servers.put(hash(server), server);
        } else {
            client = managementClients.get(hash(server));
        }
        return client;
    }

    private Client<A1Password.Client> getPasswordClient(ServerDescription server) {
        Client<A1Password.Client> client = null;
        if (!passwordClients.containsKey(hash(server))) {
            client = Client.Factory.createSimplePasswordClient(server);
            passwordClients.put(hash(server), client);
            servers.put(hash(server), server);
        } else {
            client = passwordClients.get(hash(server));
        }
        return client;
    }

    private AsyncClient<A1Management.AsyncClient> getManagementAsyncClient(ServerDescription server) {
        AsyncClient<A1Management.AsyncClient> client = null;
        if (!managementAsyncClients.containsKey(hash(server))) {
            try {
                client = AsyncClient.Factory.createManagementClient(server);
                managementAsyncClients.put(hash(server), client);
                servers.put(hash(server), server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            client = managementAsyncClients.get(hash(server));
        }
        return client;
    }

    private AsyncClient<A1Password.AsyncClient> getPasswordAsyncClient(ServerDescription server) {
        AsyncClient<A1Password.AsyncClient> client = null;
        if (!passwordAsyncClients.containsKey(hash(server))) {
            try {
                client = AsyncClient.Factory.createPasswordClient(server);
                passwordAsyncClients.put(hash(server), client);
                servers.put(hash(server), server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            client = passwordAsyncClients.get(hash(server));
        }
        return client;
    }

    private void handleClientFailed(ServerDescription server) {

        System.out.println("Removing client from client pool");

        if (managementClients.containsKey(hash(server))) {
            Client<A1Management.Client> client = managementClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            managementClients.remove(hash(server));
        }

        if (passwordClients.containsKey(hash(server))) {
            Client<A1Password.Client> client = passwordClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            passwordClients.remove(hash(server));
        }

        if (servers.containsKey(hash(server))) {
            servers.remove(hash(server));
        }
    }

    private void handleAsyncClientFailed(ServerDescription server) {

        System.out.println("Removing client from client pool");

        if (managementAsyncClients.containsKey(hash(server))) {
            AsyncClient<A1Management.AsyncClient> client = managementAsyncClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            managementAsyncClients.remove(hash(server));
        }

        if (passwordAsyncClients.containsKey(hash(server))) {
            AsyncClient<A1Password.AsyncClient> client = passwordAsyncClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            passwordAsyncClients.remove(hash(server));
        }

        if (servers.containsKey(hash(server))) {
            servers.remove(hash(server));
        }
    }

    @Override
    public void close() throws IOException {
        for (Client<A1Management.Client> client: managementClients.values()) {
            client.close();
        }

        for (Client<A1Password.Client> client: passwordClients.values()) {
            client.close();
        }

        for (AsyncClient<A1Management.AsyncClient> client: managementAsyncClients.values()) {
            client.close();
        }

        for (AsyncClient<A1Password.AsyncClient> client: passwordAsyncClients.values()) {
            client.close();
        }

        managementClients.clear();
        passwordClients.clear();
        servers.clear();
    }

    public static class Client<T extends TServiceClient> implements Closeable {
        public static class Factory {
            private static A1Management.Client.Factory managementFactory = new A1Management.Client.Factory();
            private static A1Password.Client.Factory passwordFactory = new A1Password.Client.Factory();

            public static Client<A1Management.Client> createSimpleManagementClient(String host, int mport) {
                TTransport transport = new TSocket(host, mport);
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = new A1Management.Client.Factory().getClient(protocol);

                Client<A1Management.Client> continuousClient = new Client<A1Management.Client>(transport, protocol, client);

                return continuousClient;
            }

            public static Client<A1Management.Client> createSimpleManagementClient(ServerDescription server) {
                TTransport transport = new TSocket(server.getHost(), server.getMport());
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = managementFactory.getClient(protocol);

                Client<A1Management.Client> continuousClient = new Client<A1Management.Client>(transport, protocol, client);

                return continuousClient;
            }

            public static Client<A1Password.Client> createSimplePasswordClient(ServerDescription server) {
                TTransport transport = new TSocket(server.getHost(), server.getPport());
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Password.Client client = passwordFactory.getClient(protocol);

                Client<A1Password.Client> continuousClient = new Client<A1Password.Client>(transport, protocol, client);

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

    public static class AsyncClient<T extends TAsyncClient> implements Closeable {
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

            public static AsyncClient<A1Management.AsyncClient> createManagementClient(String host, int mport) throws IOException {
                initialize();
                TNonblockingTransport transport = new TNonblockingSocket(host, mport);
                AsyncClient<A1Management.AsyncClient> client = new AsyncClient<A1Management.AsyncClient>(transport, managementFactory.getAsyncClient(transport));
                return client;
            }

            public static AsyncClient<A1Management.AsyncClient> createManagementClient(ServerDescription server) throws IOException {
                initialize();
                TNonblockingTransport transport = new TNonblockingSocket(server.getHost(), server.getMport());
                AsyncClient<A1Management.AsyncClient> client = new AsyncClient<A1Management.AsyncClient>(transport, managementFactory.getAsyncClient(transport));
                return client;
            }

            public static AsyncClient<A1Password.AsyncClient> createPasswordClient(ServerDescription server) throws IOException {
                initialize();
                TNonblockingTransport transport = new TNonblockingSocket(server.getHost(), server.getMport());
                AsyncClient<A1Password.AsyncClient> client = new AsyncClient<A1Password.AsyncClient>(transport, passwordFactory.getAsyncClient(transport));
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

        public TTransport getTransport() {
            return transport;
        }

        public T getClient() {
            return client;
        }

        @Override
        public void close() throws IOException {
            if (transport.isOpen()) {
                transport.close();
            }
        }
    }
}
