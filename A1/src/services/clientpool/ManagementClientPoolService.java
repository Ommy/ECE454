package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.transport.TTransportException;
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

    private final HashMap<String, ConcurrentLinkedQueue<Client<A1Management.Client>>> clients;

    public final IServer myServer;

    public ManagementClientPoolService(IServer server) {
        myServer = server;

        clients = new HashMap<String, ConcurrentLinkedQueue<Client<A1Management.Client>>>();
    }

    @Override
    public  <T> T callOnce(String host, int port, IManagementServiceRequest request) {
        T result = null;
        try {
            LOGGER.info("Calling with one time client connection");

            Client<A1Management.Client> client = Client.Factory.createSimpleManagementClient(host, port);
            LOGGER.info("Created client");

            client.open();
            LOGGER.info("Opened client");

            A1Management.Client mClient = client.getClient();
            LOGGER.info("Got client");


            result = request.perform(mClient);
            LOGGER.info("Completed action");

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

            Client<A1Management.Client> client = takeClient(targetServer, targetServer.getHost(), targetServer.getMport());
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
    public void close() throws IOException {
        for (ConcurrentLinkedQueue<Client<A1Management.Client>> queue: clients.values()) {
            for (Client<A1Management.Client> client: queue) {
                client.close();
            }
        }
    }

    protected synchronized Client<A1Management.Client> takeClient(ServerDescription server, String host, int port) throws TTransportException {
        Client<A1Management.Client> client = null;
        if (!clients.containsKey(hash(server))) {
            client = Client.Factory.createSimpleManagementClient(host, port);
        } else {
            client = clients.get(hash(server)).poll();
            if (client == null) {
                client = Client.Factory.createSimpleManagementClient(host, port);
            }
        }
        return client;
    }

    protected synchronized void returnClient(ServerDescription server, Client<A1Management.Client> client) {
        if (!clients.containsKey(hash(server))) {
            clients.put(hash(server), new ConcurrentLinkedQueue<Client<A1Management.Client>>());
        }

        clients.get(hash(server)).offer(client);
    }
}
