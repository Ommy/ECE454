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

public class ClientPoolService implements Closeable {
    public final IServer server;

    private HashMap<ServerDescription, Client<A1Management.Client>> managementClients;
    private HashMap<ServerDescription, Client<A1Password.Client>> passwordClients;

    public ClientPoolService(IServer server) {
        this.server = server;

        managementClients = new HashMap<ServerDescription, Client<A1Management.Client>>();
        passwordClients = new HashMap<ServerDescription, Client<A1Password.Client>>();
    }

    public <T> T callOnce(String host, int mport, IManagementServiceRequest request) {
        T result = null;
        try {
            Client<A1Management.Client> client = Client.Factory.createSimpleManagementClient(host, mport);
            result = request.perform(client.getClient());
            client.close();
        } catch (TException te) {
            te.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public <T> T call(ServerDescription server, IManagementServiceRequest request) {
        if (!managementClients.containsKey(server)) {
            createClient(server, managementClients, new A1Management.Client.Factory());
        }

        T value = null;
        try {
            value = request.perform(managementClients.get(server).getClient());
        } catch (TException te) {
            te.printStackTrace();
            handleClientFailed(server);
        }

        return value;
    }

    public <T> T call(ServerDescription server, IPasswordServiceRequest request) {
        if (!passwordClients.containsKey(server)) {
            createClient(server, passwordClients, new A1Password.Client.Factory());
        }

        T value = null;
        try {
            value = request.perform(passwordClients.get(server).getClient());
        } catch (TException te) {
            te.printStackTrace();
            handleClientFailed(server);
        }

        return value;
    }

    private <T extends TServiceClient> void createClient(ServerDescription server, HashMap<ServerDescription, Client<T>> clients, TServiceClientFactory<T> factory) {
        if (!clients.containsKey(server)) {
            Client<T> client = Client.Factory.createSimpleClient(server, factory);
            clients.put(server, client);
        }
    }

    private void handleClientFailed(ServerDescription server) {
        if (managementClients.containsKey(server)) {
            Client<A1Management.Client> client = managementClients.get(server);
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            managementClients.remove(server);
        }

        if (passwordClients.containsKey(server)) {
            Client<A1Password.Client> client = passwordClients.get(server);
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            passwordClients.remove(server);
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
    }

    public static class Client<T extends TServiceClient> implements Closeable {
        public static class Factory {

            public static Client<A1Management.Client> createSimpleManagementClient(String host, int mport) {
                TTransport transport = new TSocket(host, mport);
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = new A1Management.Client.Factory().getClient(protocol);

                Client<A1Management.Client> continuousClient = new Client<A1Management.Client>(transport, protocol, client);

                try {
                    transport.open();
                } catch (TException te) {
                    te.printStackTrace();
                }

                return continuousClient;
            }

            public static <T extends TServiceClient> Client<T> createSimpleClient(ServerDescription server, TServiceClientFactory<T> factory) {
                TTransport transport = new TSocket(server.getHost(), server.getPport());
                TProtocol protocol = new TBinaryProtocol(transport);
                T client = factory.getClient(protocol);

                Client<T> continuousClient = new Client<T>(transport, protocol, client);

                try {
                    transport.open();
                } catch (TException te) {
                    te.printStackTrace();
                }

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

        @Override
        public void close() throws IOException {
            transport.close();
        }
    }

}
