package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import servers.IServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;

public class ClientPoolService implements Closeable {
    public final IServer server;

    private HashMap<String, ServerDescription> servers;
    private HashMap<String, Client<A1Management.Client>> managementClients;
    private HashMap<String, Client<A1Password.Client>> passwordClients;

    public ClientPoolService(IServer server) {
        this.server = server;

        servers = new HashMap<String, ServerDescription>();
        managementClients = new HashMap<String, Client<A1Management.Client>>();
        passwordClients = new HashMap<String, Client<A1Password.Client>>();
    }

    private String hash(ServerDescription server) {
        return server.getHost() + "," + server.getPport() + "," + server.getMport();
    }

    public <T> T callOnce(String host, int mport, IManagementServiceRequest request) {
        T result = null;
        try {
            Client<A1Management.Client> client = Client.Factory.createSimpleManagementClient(host, mport);
            result = request.perform(client.getClient());
            client.close();
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

    public <T> T call(ServerDescription server, IManagementServiceRequest request) {
        System.out.println("ClientPoolService call");

        if (!managementClients.containsKey(hash(server))) {
            createClient(server, managementClients, new A1Management.Client.Factory());
        }

        T value = null;
        try {
            System.out.println("Perform");

            final A1Management.Iface client = managementClients.get(hash(server)).getClient();
            System.out.println(client.toString());
            value = request.perform(client);

            System.out.println("Performed");
        } catch (TException te) {
            System.out.println("Failed");
            te.printStackTrace();
            handleClientFailed(server);
        }

        return value;
    }

    public <T> T call(ServerDescription server, IPasswordServiceRequest request) {
        System.out.println("ClientPoolService call");

        if (!passwordClients.containsKey(hash(server))) {
            createClient(server, passwordClients, new A1Password.Client.Factory());
        }

        T value = null;
        try {
            value = request.perform(passwordClients.get(hash(server)).getClient());
        } catch (TException te) {
            te.printStackTrace();
            handleClientFailed(server);
        }

        return value;
    }

    private <T extends TServiceClient> void createClient(ServerDescription server, HashMap<String, Client<T>> clients, TServiceClientFactory<T> factory) {
        if (!clients.containsKey(hash(server))) {
            Client<T> client = Client.Factory.createSimpleClient(server, factory);
            clients.put(hash(server), client);
            servers.put(hash(server), server);
        }
    }

    private void handleClientFailed(ServerDescription server) {
        if (managementClients.containsKey(hash(server))) {
            Client<A1Management.Client> client = managementClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            managementClients.remove(hash(server));
            servers.remove(hash(server));
        }

        if (passwordClients.containsKey(hash(server))) {
            Client<A1Password.Client> client = passwordClients.get(hash(server));
            try {
                client.close();
            } catch (IOException te) {
                te.printStackTrace();
            }
            passwordClients.remove(hash(server));
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
    }

    public static class Client<T extends TServiceClient> implements Closeable {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Client<?> client1 = (Client<?>) o;

            if (transport != null ? !transport.equals(client1.transport) : client1.transport != null) return false;
            if (protocol != null ? !protocol.equals(client1.protocol) : client1.protocol != null) return false;
            return !(client != null ? !client.equals(client1.client) : client1.client != null);

        }

        @Override
        public int hashCode() {
            int result = transport != null ? transport.hashCode() : 0;
            result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
            result = 31 * result + (client != null ? client.hashCode() : 0);
            return result;
        }

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
