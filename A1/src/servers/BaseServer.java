package servers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import requests.IManagementServiceRequest;
import services.*;
import utilities.ServerDescriptionParser;

import java.util.*;
import java.util.concurrent.*;

public abstract class BaseServer implements IServer {

    private static final ServerDescriptionParser parser = new ServerDescriptionParser();
    private final ServiceExecutor serviceExecutor;

    private final ServerData myData;
    private final ServerDescription myDescription;

    private final List<String> seedHosts;
    private final List<Integer> seedPorts;

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

        myData = new ServerData(new CopyOnWriteArrayList<ServerDescription>((Arrays.asList(myDescription))), new CopyOnWriteArrayList<ServerDescription>());
    }

    @Override
    public synchronized boolean isSeedNode(String host, int mport) {
        boolean isSeed = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            if (seedHosts.get(i).equals(host) && seedPorts.get(i).equals(mport)) {
                isSeed = true;
            }
        }
        return isSeed;
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
        List<ServerDescription> myOffline = myData.getOfflineServers();

        System.out.println("Updating data for " + myDescription.toString());
        System.out.println("Online servers: " + myData.getOnlineServersSize() + " :::: " + myData.getOnlineServers());
        System.out.println("Offline servers: " + myData.getOfflineServersSize() + " :::: " + myData.getOfflineServers());

        // update my list of online servers

        // TODO Figure out how to handle killed and restarting nodes
        for (ServerDescription onlineServer: theirData.getOnlineServers()) {
            if (!myOnline.contains(onlineServer)) {
                myOnline.add(onlineServer);
            }
        }

        // update my list of offline servers
        for (ServerDescription offlineServer: theirData.getOfflineServers()) {
            if (!myOffline.contains(offlineServer)) {
                myOffline.add(offlineServer);
            }
        }

        // remove servers that are offline
        myOnline.removeAll(myOffline);

        System.out.println("Updated data for: " + myDescription.toString());
        System.out.println("Online servers: " + myData.getOnlineServersSize() + " :::: " + myData.getOnlineServers());
        System.out.println("Offline servers: " + myData.getOfflineServersSize() + " :::: " + myData.getOfflineServers());
    }

    @Override
    public synchronized void onConnectionFailed(ServerDescription failedServer) {
        myData.getOnlineServers().remove(failedServer);
        myData.getOfflineServers().add(failedServer);
    }

    @Override
    public synchronized ServiceExecutor getServiceExecutor() {
        return serviceExecutor;
    }

    private void registerWithSeedNodes() {
        System.out.println("Registering with the seed nodes");

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
                                ServerData theirData = client.exchangeServerData(myData);
                                return theirData;
                            }
                        });

                        if (theirData != null) {
                            server.updateData(theirData);
                        }

                        System.out.println("Completed first registration handshake with seed");
                        return null;
                    }
                });
            }
        }

        try {
            executor.invokeAny(workers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("Completed registering with any endpoint");
    }

    private void initializeEndpoints(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        System.out.println("Initializing endpoints");

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
            System.out.println("Continuous running endpoints failed");
            e.printStackTrace();
        }

        System.out.println("Unexpected quit running endpoints failed");
    }

    protected void run(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        registerWithSeedNodes();
        initializeEndpoints(pHandler, mHandler);
    }
}
