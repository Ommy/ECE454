package servers;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerStatus;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;

import java.lang.Runnable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server {

    protected List<ServerDescription> peers;
    protected final ServerDescription description;

    protected final List<String> seedHosts;
    protected final List<Integer> seedPorts;

    protected Server(String[] args) {
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();

        String host = "localhost";
        int pport = 6719;
        int mport = 4848;
        int ncores = 1;

        List<String> seedsList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && i+1 < args.length) {
                host = args[i+1];
            } else if (args[i].equals("-pport") && i+1 < args.length) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-mport") && i+1 < args.length) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-ncores") && i+1 < args.length) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-seeds") && i+1 < args.length) {
                seedsList = Arrays.asList(args[i + 1].split(","));
            }
        }

        description = new ServerDescription(host, pport, mport, ncores, ServerStatus.UNREGISTERED);

        for(String seed: seedsList) {
            String[] splitSeed = seed.split(":");
            seedHosts.add(splitSeed[0]);
            seedPorts.add(Integer.parseInt(splitSeed[1]));
        }

        peers = new LinkedList<ServerDescription>();
    }

    public void onStartupRegister() {
        try {
            if (!isSeedNode()) {

                final ExecutorService executor = Executors.newFixedThreadPool(seedHosts.size());
                final List<Callable> workers = new ArrayList<Callable>();

                for (int i = 0; i < seedHosts.size(); ++i) {
                    final String seedHost = seedHosts.get(i);
                    final int seedPort = seedPorts.get(i);

                    // type should not be void
                    workers.add(new Callable<Void>() {

                        @Override
                        public Void call() {
                            try {
                                TTransport transport = new TSocket(seedHost, seedPort);
                                TProtocol protocol = new TBinaryProtocol(transport);

                                transport.open();
                                A1Management.Client client = new A1Management.Client(protocol);

                                // retrieve nodes from seed list

                                transport.close();

                            } catch(TException te) {
                                te.printStackTrace();
                            }

                            return null;
                        }
                    });
                }

                executor.invokeAny(workers);

                // startup
                // peers.add();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onStartupInitializeServices(final A1Password.Processor passwordProcessor, final A1Management.Processor managementProcessor) {

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            final Callable managementRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    try {

                        TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getMport());
                        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

                        args.transportFactory(new TFramedTransport.Factory());
                        args.protocolFactory(new TBinaryProtocol.Factory());
                        args.processor(managementProcessor);
                        args.selectorThreads(8*description.getNcores());
                        args.workerThreads(8*description.getNcores());

                        TServer server = new TThreadedSelectorServer(args);
                        server.serve();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            final Callable passwordRunnable = new Callable<Void>() {

                @Override
                public Void call() {
                    try {

                        TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getMport());
                        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

                        args.transportFactory(new TFramedTransport.Factory());
                        args.protocolFactory(new TBinaryProtocol.Factory());
                        args.processor(passwordProcessor);
                        args.selectorThreads(8*description.getNcores());
                        args.workerThreads(8*description.getNcores());

                        TServer server = new TThreadedSelectorServer(args);
                        server.serve();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            executor.invokeAll(new ArrayList<Callable<Void>>(){ managementRunnable, passwordRunnable});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSeedNode() {
        boolean isSeed = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            if (seedHosts.get(i).equals(description.getHost()) && seedPorts.get(i).equals(description.getMport())) {
                isSeed = true;
            }
        }
        return isSeed;
    }

}
