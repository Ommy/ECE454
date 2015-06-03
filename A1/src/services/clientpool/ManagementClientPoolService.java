package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import servers.IServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ManagementClientPoolService extends BaseClientPoolService<A1Management.Client, A1Management.AsyncClient> implements IClientService<IManagementServiceRequest, IManagementServiceAsyncRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementClientPoolService.class.getName());

    private HashMap<String, ConcurrentLinkedQueue<Client<A1Management.Client>>> clients;
    private HashMap<String, ConcurrentLinkedQueue<AsyncClient<A1Management.AsyncClient>>> asyncClients;

    public final IServer myServer;

    public ManagementClientPoolService(IServer server) {
        myServer = server;

        clients = new HashMap<String, ConcurrentLinkedQueue<Client<A1Management.Client>>>();
        asyncClients = new HashMap<String, ConcurrentLinkedQueue<AsyncClient<A1Management.AsyncClient>>>();
    }

    @Override
    public <T> T callOnce(String host, int port, IManagementServiceRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");

            Client<A1Management.Client> client = Client.Factory.createSimpleManagementClient(host, port);
            client.open();
            result = request.perform(client.getClient());
            client.close();

            LOGGER.debug("Completed one time client connection");
        } catch (TException te) {
            // TODO: Handle errors
            LOGGER.error("Call once failed from " + myServer.getDescription() + "to host " + host + " and port " + port);
        }

        return result;
    }

    @Override
    public <T> T call(ServerDescription targetServer, IManagementServiceRequest request) {
        LOGGER.debug("ClientPoolService call - IManagementService");

        T result = null;
        try {
            LOGGER.debug("Re-using client connection");

            Client<A1Management.Client> client = takeClient(targetServer, targetServer.getHost(), targetServer.getMport(),
                                                            clients, managementFactory);
            client.open();
            result = request.perform(client.getClient());
            returnClient(targetServer, client, clients);

            LOGGER.debug("Completed re-usable client connection");
        } catch (TException te) {
            LOGGER.error("Failed to re-use client connection: ", te);
            handleClientFailed(targetServer, clients);
            myServer.onConnectionFailed(targetServer);
        }

        return result;
    }

    @Override
    public <T> T callOnceAsync(String host, int port, IManagementServiceAsyncRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");
            initialize();

            AsyncClient<A1Management.AsyncClient> client = AsyncClient.Factory.createSimpleManagementClient(host, port);
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
    public <T> T callAsync(ServerDescription targetServer, IManagementServiceAsyncRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");
            initialize();

            AsyncClient<A1Management.AsyncClient> client = AsyncClient.Factory.createSimpleManagementClient(targetServer.getHost(), targetServer.getMport());
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
    public synchronized void close() throws IOException {
        for (ConcurrentLinkedQueue<Client<A1Management.Client>> queue: clients.values()) {
            for (Client<A1Management.Client> client: queue) {
                client.close();
            }
        }

        for (ConcurrentLinkedQueue<AsyncClient<A1Management.AsyncClient>> queue: asyncClients.values()) {
            for (AsyncClient<A1Management.AsyncClient> client: queue) {
                client.close();
            }
        }
    }
}
