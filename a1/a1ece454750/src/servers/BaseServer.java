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
import java.util.concurrent.atomic.AtomicBoolean;

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
        seedHosts = new LinkedList<String>();
        seedPorts = new LinkedList<Integer>();

        myDescription = parser.parse(args, type);

        for(String seed: myDescription.getSeedsList()) {
            String[] splitSeed = seed.split(":");
            seedHosts.add(splitSeed[0]);
            seedPorts.add(Integer.parseInt(splitSeed[1]));
        }

        myPerfCounter = new PerfCounters();
        myData = new ServerData(new LinkedList<ServerDescription>(Collections.singletonList(myDescription)), new LinkedList<ServerDescription>());
    }

    @Override
    public ServerDescription getDescription() {
        return new ServerDescription(myDescription);
    }

    @Override
    public ServerData getData() {
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
        LOGGER.debug("Online servers: " + myData.getOnlineServersSize());
    }

    @Override
    public synchronized void removeDownedService(ServerDescription server) {
        LOGGER.debug("Removing downed service " + server.toString());

        final List<ServerDescription> myOnline = myData.getOnlineServers();
        if (myOnline.contains(server)) {
            myOnline.remove(server);
        }

        LOGGER.debug("Successfully removed " + server.toString());
    }

    @Override
    public void onConnectionFailed(final ServerDescription failedServer) {
        removeDownedService(failedServer);

        // Send this to all other servers
        List<ServerDescription> myOnline = new LinkedList<ServerDescription>(myData.getOnlineServers());
        if (myOnline.contains(myDescription)) {
            myOnline.remove(myDescription);
        }
        if (myOnline.contains(failedServer)) {
            myOnline.remove(myDescription);
        }

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        for (final ServerDescription online : myOnline) {
            if (online.getType() == ServerType.FE) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        serviceExecutor.requestExecuteToServer(online, new IManagementServiceRequest() {
                            @Override
                            public Void perform(A1Management.Iface client) throws TException {
                                client.serviceEndpointDown(failedServer);
                                return null;
                            }
                        });
                    }
                });
            }
        }

        executor.shutdown();
    }

    @Override
    public PerfCounters getPerfCounters() {
        return myPerfCounter;
    }

    @Override
    public ServiceExecutor getServiceExecutor() {
        return serviceExecutor;
    }

    private void registerWithSeedNodes() {
        LOGGER.info("Registering with the seed nodes");

        if (seedHosts.isEmpty()) {
            throw new IllegalArgumentException("Seed lists should never be empty");
        }

        final List<Callable<Void>> workers = new LinkedList<Callable<Void>>();
        final AtomicBoolean isAnyCompleted = new AtomicBoolean(false);

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < seedHosts.size(); ++i) {
            final String seedHost = seedHosts.get(i);
            final int seedPort = seedPorts.get(i);

            if (!(myDescription.getHost().equals(seedHost) && myDescription.getMport() == seedPort)) {
                workers.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {

                        ServerData seedData = null;
                        while (!isAnyCompleted.get()) {

                            seedData = (ServerData)serviceExecutor.requestExecuteToServer(seedHost, seedPort, new IManagementServiceRequest() {
                                @Override
                                public ServerData perform(A1Management.Iface client) throws TException {
                                    LOGGER.debug("Exchanging server data for the first time to: " + seedHost + " : " + seedPort);
                                    ServerData myData = getData();

                                    LOGGER.debug("Got my data");
                                    ServerData theirData = client.exchangeServerData(myData);

                                    LOGGER.debug("Got their data");
                                    return theirData;
                                }
                            });

                            if (seedData != null) {
                                isAnyCompleted.set(true);
                                LOGGER.debug(myDescription.toString() + ";  Completed Registration");
                            } else {
                                Thread.sleep(200);
                                LOGGER.error(myDescription.toString() + ";  Stuck in loop");
                            }
                        }

                        if (seedData != null) {
                            updateData(seedData);
                        }

                        LOGGER.info("Completed first registration handshake with seed");
                        return null;
                    }
                });
            }
        }

        if (!isSeedNode() && !workers.isEmpty()) {
            try {
                executor.invokeAny(workers);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdownNow();

        LOGGER.info("Completed registering with any endpoint");
    }

    private void initializeEndpoints(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        LOGGER.info("Initializing endpoints");


        final EndpointProvider endpointProvider = new EndpointProvider();
        final ExecutorService threadedExecutor = Executors.newFixedThreadPool(2);
        final Runnable managementRunnable = new Runnable() {
            @Override
            public void run() {
                if (myDescription.getType() == ServerType.BE) {
                    endpointProvider.serveBEManagementEndpoint(myDescription, mHandler);
                } else {
                    endpointProvider.serveFEManagementEndpoint(myDescription, mHandler);
                }
            }
        };
        final Runnable passwordRunnable = new Runnable() {
            @Override
            public void run() {
                if (myDescription.getType() == ServerType.BE) {
                    endpointProvider.serveBEPasswordEndpoint(myDescription, pHandler);
                } else {
                    endpointProvider.serveFEPasswordEndpoint(myDescription, pHandler);
                }
            }
        };

        threadedExecutor.execute(managementRunnable);
        threadedExecutor.execute(passwordRunnable);
    }

    private void beginGossip() {
        final ScheduledExecutorService scheduledThreadPool;
        if (myDescription.getType() == ServerType.FE) {
            scheduledThreadPool = Executors.newScheduledThreadPool(1);
            final GossipService gossipService = new GossipService(this);
            final Runnable gossipRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        gossipService.gossip();
                    } catch (ServiceUnavailableException e) {
                        // just idle if there are no servers to gossip with
                    }
                }
            };

            scheduledThreadPool.scheduleWithFixedDelay(gossipRunnable, 100, 100, TimeUnit.MILLISECONDS);
        }
    }


    protected void run(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        registerWithSeedNodes();
        initializeEndpoints(pHandler, mHandler);
        beginGossip();

        LOGGER.info("Running...");

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        System.out.println("THREADS:::: " + threadSet.toString());
    }

    private boolean isSeedNode() {
        boolean isSeedNode = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            final String seedHost = seedHosts.get(i);
            final int seedPort = seedPorts.get(i);
            if (myDescription.getHost().equals(seedHost) && myDescription.getMport() == seedPort) {
                isSeedNode = true;
                break;
            }
        }
        return isSeedNode;
    }
}
