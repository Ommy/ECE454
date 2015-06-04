package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseClientPoolService<T extends TServiceClient, TA extends TAsyncClient> implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseClientPoolService.class.getName());

    protected static A1Management.Client.Factory managementFactory = new A1Management.Client.Factory();
    protected static A1Password.Client.Factory passwordFactory = new A1Password.Client.Factory();
    protected static A1Management.AsyncClient.Factory asyncManagementFactory = null;
    protected static A1Password.AsyncClient.Factory asyncPasswordFactory = null;

    protected static void initialize() throws IOException {
        if (asyncManagementFactory == null) {
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TAsyncClientManager clientManager = new TAsyncClientManager();
            asyncManagementFactory = new A1Management.AsyncClient.Factory(clientManager, protocolFactory);
        }

        if (asyncPasswordFactory == null) {
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TAsyncClientManager clientManager = new TAsyncClientManager();
            asyncPasswordFactory = new A1Password.AsyncClient.Factory(clientManager, protocolFactory);
        }
    }


    private String hash(ServerDescription server) {
        return server.getHost() + "," + server.getPport() + "," + server.getMport();
    }

    protected <T extends TServiceClient> Client<T> takeClient(ServerDescription server, String host, int port, HashMap<String, ConcurrentLinkedQueue<Client<T>>> map, TServiceClientFactory<T> factory) {
        Client<T> client = null;
        if (!map.containsKey(hash(server))) {
            client = Client.Factory.createSimpleClient(host, port, factory);
        } else {
            client = map.get(hash(server)).poll();
            if (client == null) {
                client = Client.Factory.createSimpleClient(host, port, factory);
            }
        }
        return client;
    }

    protected <T extends TServiceClient> void returnClient(ServerDescription server, Client<T> client, HashMap<String, ConcurrentLinkedQueue<Client<T>>> map) {
        if (!map.containsKey(hash(server))) {
            map.put(hash(server), new ConcurrentLinkedQueue<Client<T>>());
        }

        map.get(hash(server)).offer(client);
    }

    protected <T extends TAsyncClient> AsyncClient<T> takeAsyncClient(ServerDescription server, String host, int port, HashMap<String, ConcurrentLinkedQueue<AsyncClient<T>>> map, TAsyncClientFactory<T> factory) throws IOException {
        AsyncClient<T> client = null;
        if (!map.containsKey(hash(server))) {
            client = AsyncClient.Factory.createSimpleClient(host, port, factory);
        } else {
            client = map.get(hash(server)).poll();
            if (client == null) {
                client = AsyncClient.Factory.createSimpleClient(host, port, factory);
            }
        }
        return client;
    }

    protected <T extends TAsyncClient> void returnAsyncClient(ServerDescription server, AsyncClient<T> client, HashMap<String, ConcurrentLinkedQueue<AsyncClient<T>>> map) {
        if (!map.containsKey(hash(server))) {
            map.put(hash(server), new ConcurrentLinkedQueue<AsyncClient<T>>());
        }

        map.get(hash(server)).offer(client);
    }

    protected <T extends TServiceClient> void handleClientFailed(ServerDescription server, HashMap<String, ConcurrentLinkedQueue<Client<T>>> map) {
        LOGGER.debug("Removing client from client pool");

        if (map.containsKey(hash(server))) {
            ConcurrentLinkedQueue<Client<T>> queue = map.get(hash(server));
            for (Client<T> client: queue) {
                client.close();
            }
            map.remove(hash(server));
        }
    }

    protected synchronized <T extends TAsyncClient> void handleAsyncClientFailed(ServerDescription server, HashMap<String, ConcurrentLinkedQueue<AsyncClient<T>>> map) {
        LOGGER.debug("Removing client from client pool");

        if (map.containsKey(hash(server))) {
            map.remove(hash(server));
        }
    }
}
