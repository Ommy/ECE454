package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;
import servers.IServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PasswordClientPoolService extends BaseClientPoolService<A1Password.Client, A1Password.AsyncClient> implements IClientService<IPasswordServiceRequest, IPasswordServiceAsyncRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordClientPoolService.class.getName());

    private HashMap<String, ConcurrentLinkedQueue<Client<A1Password.Client>>> clients;
    private HashMap<String, ConcurrentLinkedQueue<AsyncClient<A1Password.AsyncClient>>> asyncClients;

    public final IServer myServer;

    public PasswordClientPoolService(IServer server) {
        myServer = server;

        clients = new HashMap<String, ConcurrentLinkedQueue<Client<A1Password.Client>>>();
        asyncClients = new HashMap<String, ConcurrentLinkedQueue<AsyncClient<A1Password.AsyncClient>>>();
    }

    @Override
    public <T> T callOnce(String host, int port, IPasswordServiceRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");

            Client<A1Password.Client> client = Client.Factory.createSimplePasswordClient(host, port);
            client.open();
            result = request.perform(client.getClient());
            client.close();

            LOGGER.debug("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", te);
        }
        return result;
    }

    @Override
    public <T> T call(ServerDescription targetServer, IPasswordServiceRequest request) {
        LOGGER.debug("ClientPoolService call - IPasswordService");

        T result = null;
        try {
            LOGGER.debug("Re-using client connection");

            Client<A1Password.Client> client = takeClient(targetServer, targetServer.getHost(), targetServer.getPport());
            client.open();
            result = request.perform(client.getClient());
            returnClient(targetServer, client);

            LOGGER.debug("Completed re-usable client connection");
        } catch (TException te) {
            LOGGER.error("Failed to re-use client connection: ", te);

            handleClientFailed(targetServer, clients);
            myServer.onConnectionFailed(targetServer);
        }

        return result;
    }

    @Override
    public <T> T callOnceAsync(String host, int port, IPasswordServiceAsyncRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");
            initialize();

            AsyncClient<A1Password.AsyncClient> client = AsyncClient.Factory.createSimplePasswordClient(host, port);
            client.open();
            result = request.perform(client.getClient());
            client.close();

            LOGGER.debug("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", te);
        } catch (IOException e) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", e);
        }
        return result;
    }

    @Override
    public <T> T callAsync(ServerDescription targetServer, IPasswordServiceAsyncRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");
            initialize();

            AsyncClient<A1Password.AsyncClient> client = AsyncClient.Factory.createSimplePasswordClient(targetServer.getHost(), targetServer.getPport());
            client.open();
            result = request.perform(client.getClient());
            client.close();

            LOGGER.debug("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", te);
            handleAsyncClientFailed(targetServer, asyncClients);
            myServer.onConnectionFailed(targetServer);
        } catch (IOException e) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", e);
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        for (ConcurrentLinkedQueue<Client<A1Password.Client>> queue: clients.values()) {
            for (Client<A1Password.Client> client: queue) {
                client.close();
            }
        }
        for (ConcurrentLinkedQueue<AsyncClient<A1Password.AsyncClient>> queue: asyncClients.values()) {
            for (AsyncClient<A1Password.AsyncClient> client: queue) {
                client.close();
            }
        }
    }

    protected Client<A1Password.Client> takeClient(ServerDescription server, String host, int port) throws TTransportException {
        Client<A1Password.Client> client = null;
        if (!clients.containsKey(hash(server))) {
            client = Client.Factory.createSimplePasswordClient(host, port);
        } else {
            client = clients.get(hash(server)).poll();
            if (client == null) {
                client = Client.Factory.createSimplePasswordClient(host, port);
            }
        }
        return client;
    }

    protected void returnClient(ServerDescription server, Client<A1Password.Client> client) {
        if (!clients.containsKey(hash(server))) {
            clients.put(hash(server), new ConcurrentLinkedQueue<Client<A1Password.Client>>());
        }

        clients.get(hash(server)).offer(client);
    }
}
