package servers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.server.TThreadPoolServer;
import services.EndpointProvider;
import services.IManagementServiceRequest;
import services.ServiceExecutor;
import services.SimpleScheduler;

import java.util.*;
import java.util.concurrent.*;

public abstract class BaseServer implements IServer {

    private final ServiceExecutor serviceExecutor;

    private final ServerData myData;
    private final ServerDescription myDescription;

    private final List<String> seedHosts;
    private final List<Integer> seedPorts;

    protected BaseServer(String[] args, ServerType type) {

        serviceExecutor = new ServiceExecutor(this, new SimpleScheduler(this));
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();

        String host = "localhost";
        int pport = 6719;
        int mport = 4848;
        int ncores = 1;

        List<String> seedsList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && (i+1 < args.length)) {
                host = args[i+1];
            } else if (args[i].equals("-pport") && (i+1 < args.length)) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-mport") && (i+1 < args.length)) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-ncores") && (i+1 < args.length)) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-seeds") && (i+1 < args.length)) {
                seedsList = Arrays.asList(args[i+1].split(","));
            }
        }

        for(String seed: seedsList) {
            String[] splitSeed = seed.split(":");
            seedHosts.add(splitSeed[0]);
            seedPorts.add(Integer.parseInt(splitSeed[1]));
        }

        myDescription = new ServerDescription(host, pport, mport, ncores, type);
        myData = new ServerData(Arrays.asList(myDescription), new ArrayList<ServerDescription>());
    }

    @Override
    public boolean isSeedNode(String host, int mport) {
        boolean isSeed = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            if (seedHosts.get(i).equals(host) && seedPorts.get(i).equals(mport)) {
                isSeed = true;
            }
        }
        return isSeed;
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
    public void updateData(ServerData theirData) {
        List<ServerDescription> myOnline = myData.getOnlineServers();
        List<ServerDescription> myOffline = myData.getOfflineServers();

        // update my list of online servers
        for (ServerDescription onlineServer: theirData.getOnlineServers()) {
            System.out.println("Updating online data..." + onlineServer.toString());

            if (!myOnline.contains(onlineServer)) {
                myOnline.add(onlineServer);
            }
        }

        System.out.println("Updating offline data...");
        // update my list of offline servers
        for (ServerDescription offlineServer: theirData.getOfflineServers()) {
            System.out.println("Updating offline data..." + offlineServer.toString());
            if (!myOffline.contains(offlineServer)) {
                myOffline.add(offlineServer);
            }
        }

        System.out.println("Updating offline data...");

        // remove servers that are offline
        myOnline.removeAll(myOffline);

        System.out.println("Completed updating data...");
    }

    private boolean isSeedNode() {
        return isSeedNode(myDescription.getHost(), myDescription.getMport());
    }

    private void registerWithSeedNodes() {

        System.out.println("Registering");

        if (isSeedNode()) {
            System.out.println("Seed nodes don't need to register");
            return;
        }

        System.out.println("Still registering");

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
                        server.updateData(theirData);
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

        System.out.println("Registered any endpoint");
    }

    private void initializeEndpoints(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {

        final A1Password.Processor pProcessor = new A1Password.Processor<A1Password.Iface>(pHandler);
        final A1Management.Processor mProcessor = new A1Management.Processor<A1Management.Iface>(mHandler);
        final EndpointProvider endpointProvider = new EndpointProvider();

        try {
            // TODO: Do we need to run these on a new thread?
            ExecutorService executor = Executors.newFixedThreadPool(2);

            final Callable<Void> managementRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    endpointProvider.serveManagementEndpoint(myDescription, mProcessor);
                    return null;
                }
            };

            final Callable<Void> passwordRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    endpointProvider.servePasswordEndpoint(myDescription, pProcessor);
                    return null;
                }
            };

            executor.invokeAll(Arrays.asList(managementRunnable, passwordRunnable));

        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    protected void run(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        registerWithSeedNodes();
        initializeEndpoints(pHandler, mHandler);
    }
}
