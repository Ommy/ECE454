package services.clientpool;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
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
        } catch (IOException e) {
            // TODO: Handle errors
            LOGGER.error("callOnce failed: ", e);
        }
        return result;
    }

    @Override
    public <T> T call(ServerDescription targetServer, IPasswordServiceRequest request) {
        LOGGER.debug("ClientPoolService call - IPasswordService");

        T result = null;
        try {
            LOGGER.debug("Re-using client connection");

            initialize();
            Client<A1Password.Client> client = takeClient(targetServer, targetServer.getHost(), targetServer.getPport(), clients, passwordFactory);
            client.open();
            result = request.perform(client.getClient());
            returnClient(targetServer, client, clients);

            LOGGER.debug("Completed re-usable client connection");
        } catch (TException te) {
            LOGGER.error("Failed to re-use client connection: ", te);
            handleClientFailed(targetServer, clients);
            myServer.onConnectionFailed(targetServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public <T> T callOnceAsync(String host, int port, IPasswordServiceAsyncRequest request) {
        T result = null;
        try {
            LOGGER.debug("Calling with one time client connection");

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
}
