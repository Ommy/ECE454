package servers;

import ece454750s15a1.*;
import org.apache.thrift.TException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requests.IManagementServiceRequest;
import services.*;
import utilities.ServerDescriptionParser;

import java.util.*;
import java.util.concurrent.*;

public abstract class BaseServer implements IServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServer.class.getName());

    private static final ServerDescriptionParser parser = new ServerDescriptionParser();
    private final ServiceExecutor serviceExecutor;

    private final ServerData myData;
    private final ServerDescription myDescription;

    private final List<String> seedHosts;
    private final List<Integer> seedPorts;

    public final PerfCounters myPerfCounter;

    protected BaseServer(String[] args, ServerType type) {

        serviceExecutor = new ServiceExecutor(this);
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();


        myDescription = parser.parse(args, type);

        for(String seed: myDescription.getSeedsList()) {
            String[] splitSeed = seed.split(":");
            seedHosts.add(splitSeed[0]);
            seedPorts.add(Integer.parseInt(splitSeed[1]));
        }

        myPerfCounter = new PerfCounters();
        myData = new ServerData(new CopyOnWriteArrayList<ServerDescription>((Arrays.asList(myDescription))), new CopyOnWriteArrayList<ServerDescription>());
    }

    @Override
    public synchronized ServerDescription getDescription() {
        return new ServerDescription(myDescription);
    }

    @Override
    public synchronized ServerData getData() {
        return new ServerData(myData);
    }

    @Override
    public synchronized void updateData(ServerData theirData) {
        List<ServerDescription> myOnline = myData.getOnlineServers();

        // update my list of online servers
        // TODO Figure out how to handle killed and restarting nodes
        for (ServerDescription onlineServer: theirData.getOnlineServers()) {
            if (!myOnline.contains(onlineServer)) {
                myOnline.add(onlineServer);
            }
        }

        LOGGER.debug("Updated data for: " + myDescription.toString());
        LOGGER.debug("Online servers: " + myData.getOnlineServers());
    }

    @Override
    public synchronized void removeDownedService(ServerDescription server) {
        LOGGER.debug("Removing downed service " + server.toString());

        List<ServerDescription> myOnline = myData.getOnlineServers();
        if (myOnline.contains(server)) {
            myOnline.remove(server);
        }

        myData.setOnlineServers(myOnline);

        LOGGER.debug("Successfully removed " + server.toString());
    }

    @Override
    public synchronized void onConnectionFailed(final ServerDescription failedServer) {
        removeDownedService(failedServer);

        // Send this to all other servers
        List<ServerDescription> myOnline = new ArrayList<ServerDescription>(myData.getOnlineServers());
        if (myOnline.contains(myDescription)) {
            myOnline.remove(myDescription);
        }
        if (myOnline.contains(failedServer)) {
            myOnline.remove(myDescription);
        }

        ExecutorService executor = Executors.newFixedThreadPool(myOnline.size());
        final List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
        for (final ServerDescription online : myOnline) {
            if (online.getType() == ServerType.FE) {
                workers.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        serviceExecutor.requestExecuteToServer(online, new IManagementServiceRequest() {
                            @Override
                            public Void perform(A1Management.Iface client) throws TException {
                                client.serviceEndpointDown(failedServer);
                                return null;
                            }
                        });
                        return null;
                    }
                });
            }
        }

        try {
            if (!workers.isEmpty()) {
                executor.invokeAny(workers);
            }
        } catch (InterruptedException ie) {
            LOGGER.error("Executor threw an exception", ie);
        } catch (ExecutionException e) {
            LOGGER.error("Executor threw an exception", e);
        }
    }

    @Override
    public synchronized PerfCounters getPerfCounters() {
        return myPerfCounter;
    }

    @Override
    public synchronized ServiceExecutor getServiceExecutor() {
        return serviceExecutor;
    }

    private void registerWithSeedNodes() {
        LOGGER.debug("Registering with the seed nodes");

        if (seedHosts.isEmpty()) {
            throw new IllegalArgumentException("Seed lists should never be empty");
        }

        ExecutorService executor = Executors.newFixedThreadPool(seedHosts.size());
        final List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
        final IServer server = this;
        for (int i = 0; i < seedHosts.size(); ++i) {
            final String seedHost = seedHosts.get(i);
            final int seedPort = seedPorts.get(i);

            if (!(myDescription.getHost().equals(seedHost) && myDescription.getMport() == seedPort)) {
                workers.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ServerData theirData = serviceExecutor.requestExecuteToServer(seedHost, seedPort, new IManagementServiceRequest() {
                            @Override
                            public ServerData perform(A1Management.Iface client) throws TException {
                                return client.exchangeServerData(getData());
                            }
                        });

                        if (theirData != null) {
                            server.updateData(theirData);
                        }

                        LOGGER.debug("Completed first registration handshake with seed");
                        return null;
                    }
                });
            }
        }
        try {
            if (!workers.isEmpty()) {
                executor.invokeAny(workers);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            LOGGER.error("No workers found. Worker list size: " + workers.size(), e);
        }

        LOGGER.debug("Completed registering with any endpoint");
    }

    private void initializeEndpoints(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        LOGGER.debug("Initializing endpoints");

        final EndpointProvider endpointProvider = new EndpointProvider();
        final GossipService gossipService = new GossipService(this);

        try {
            // TODO: Do we need to run these on a new thread?
            ExecutorService threadedExecutor = Executors.newFixedThreadPool(3);
            List<Callable<Void>> servers = new ArrayList<Callable<Void>>();

            final Callable<Void> managementRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    endpointProvider.serveManagementEndpoint(myDescription, mHandler);
                    return null;
                }
            };
            servers.add(managementRunnable);

            final Callable<Void> passwordRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    endpointProvider.servePasswordEndpoint(myDescription, pHandler);
                    return null;
                }
            };
            servers.add(passwordRunnable);

            final Callable<Void> gossipRunnable = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    gossipService.gossip();
                    return null;
                }
            };

            if (myDescription.getType() != ServerType.BE) {
                servers.add(gossipRunnable);
            }

            threadedExecutor.invokeAll(servers);

        } catch (Exception e) {
            // TODO: Handle exception
            LOGGER.error("Continuous running endpoints failed: ", e);
        }

        LOGGER.debug("Unexpected quit running endpoints failed");
    }

    protected void run(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        registerWithSeedNodes();
        initializeEndpoints(pHandler, mHandler);
    }
}
