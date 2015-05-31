package services;

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
import servers.IServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

public class ClientPoolService implements Closeable, IClientService {
    public final IServer myServer;

    private HashMap<String, ServerDescription> servers;
    private HashMap<String, Client<A1Management.Client>> managementClients;
    private HashMap<String, Client<A1Password.Client>> passwordClients;

    public ClientPoolService(IServer server) {
        this.myServer = server;

        servers = new HashMap<String, ServerDescription>();
        managementClients = new HashMap<String, Client<A1Management.Client>>();
        passwordClients = new HashMap<String, Client<A1Password.Client>>();
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

    private Client<A1Management.Client> getManagementClient(ServerDescription server) {
        Client<A1Management.Client> client = null;
        if (!managementClients.containsKey(hash(server))) {
            client = Client.Factory.createSimpleManagementClient(server, new A1Management.Client.Factory());
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
            client = Client.Factory.createSimplePasswordClient(server, new A1Password.Client.Factory());
            passwordClients.put(hash(server), client);
            servers.put(hash(server), server);
        } else {
            client = passwordClients.get(hash(server));
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

    @Override
    public void close() throws IOException {
        for (Client<A1Management.Client> client: managementClients.values()) {
            client.close();
        }

        for (Client<A1Password.Client> client: passwordClients.values()) {
            client.close();
        }

        managementClients.clear();
        passwordClients.clear();
        servers.clear();
    }

    public static class Client<T extends TServiceClient> implements Closeable {
        public static class Factory {

            public static Client<A1Management.Client> createSimpleManagementClient(String host, int mport) {
                TTransport transport = new TSocket(host, mport);
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = new A1Management.Client.Factory().getClient(protocol);

                Client<A1Management.Client> continuousClient = new Client<A1Management.Client>(transport, protocol, client);

                return continuousClient;
            }

            public static <T extends TServiceClient> Client<T> createSimpleManagementClient(ServerDescription server, TServiceClientFactory<T> factory) {
                TTransport transport = new TSocket(server.getHost(), server.getMport());
                TProtocol protocol = new TBinaryProtocol(transport);
                T client = factory.getClient(protocol);

                Client<T> continuousClient = new Client<T>(transport, protocol, client);

                return continuousClient;
            }

            public static <T extends TServiceClient> Client<T> createSimplePasswordClient(ServerDescription server, TServiceClientFactory<T> factory) {
                TTransport transport = new TSocket(server.getHost(), server.getPport());
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

        public void open() throws TException {
            if (!transport.isOpen()) {
                transport.open();
            }
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

        @Override
        public void close() throws IOException {
            transport.close();
        }
    }

}
