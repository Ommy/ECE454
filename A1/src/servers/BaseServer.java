package servers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import services.IManagementServiceRequest;
import services.ServiceExecutor;
import services.SimpleScheduler;

import java.util.*;
import java.util.concurrent.*;

public abstract class BaseServer implements IServer {

    private final ServiceExecutor executor;

    private final ServerData myData;
    private final ServerDescription myDescription;

    private final List<String> seedHosts;
    private final List<Integer> seedPorts;

    protected BaseServer(String[] args, ServerType type) {
        executor = new ServiceExecutor(this, new SimpleScheduler(this));
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
        myData = new ServerData(Arrays.asList(myDescription), new CopyOnWriteArrayList<ServerDescription>());
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
    public void updateData(ServerData data) {
        // handle new data
        myData.onlineServers.addAll(data.onlineServers);
    }

    private boolean isSeedNode() {
        return isSeedNode(myDescription.getHost(), myDescription.getMport());
    }

    private void onStartupRegister() {
        if (isSeedNode()) {
            return;
        }

        // add self to list of online servers
        myData.addToOnlineServers(myDescription);

        final List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
        for (int i = 0; i < seedHosts.size(); ++i) {
            final String seedHost = seedHosts.get(i);
            final int seedPort = seedPorts.get(i);

            executor.requestExecute(new IManagementServiceRequest() {
                @Override
                public ServerData perform(A1Management.Iface client) throws TException {
                    ServerData theirData = client.exchangeServerData(myData);
                    return theirData;
                }
            });
        }
    }

    private void onStartupInitializeServices(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {

        final A1Password.Processor pProcessor = new A1Password.Processor<A1Password.Iface>(pHandler);
        final A1Management.Processor mProcessor = new A1Management.Processor<A1Management.Iface>(mHandler);

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);

            final Callable<Void> managementRunnable = new Callable<Void>() {



                @Override
                public Void call() {
                    try {

                        TNonblockingServerTransport transport = new TNonblockingServerSocket(myDescription.getMport());
                        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

                        args.transportFactory(new TFramedTransport.Factory());
                        args.protocolFactory(new TBinaryProtocol.Factory());
                        args.processor(mProcessor);
                        args.selectorThreads(8*myDescription.getNcores());
                        args.workerThreads(8*myDescription.getNcores());

                        TServer server = new TThreadedSelectorServer(args);
                        server.serve();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            final Callable<Void> passwordRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    try {

                        TNonblockingServerTransport transport = new TNonblockingServerSocket(myDescription.getMport());
                        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

                        args.transportFactory(new TFramedTransport.Factory());
                        args.protocolFactory(new TBinaryProtocol.Factory());
                        args.processor(pProcessor);
                        args.selectorThreads(8*myDescription.getNcores());
                        args.workerThreads(8*myDescription.getNcores());

                        TServer server = new TThreadedSelectorServer(args);
                        server.serve();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            executor.invokeAll(Arrays.asList(managementRunnable, passwordRunnable));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void run(final A1Password.Iface pHandler, final A1Management.Iface mHandler) {
        onStartupRegister();

        onStartupInitializeServices(pHandler, mHandler);
    }
}
