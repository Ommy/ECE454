package servers;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerStatus;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.lang.Runnable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Server {
    public ServerDescription description;
    public List<String> seedHosts;
    public List<Integer> seedPorts;

    public void initialize(String[] args) {
        String host = "localhost";
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();
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

    }

    public boolean register() throws TException {
        AtomicBoolean status = new AtomicBoolean(false);
        AtomicBoolean completed = new AtomicBoolean(false);

        if (!isSeedNode()) {
            ExecutorService executor = Executors.newFixedThreadPool(seedHosts.size());

            for (int i = 0; (i < seedHosts.size()) && (status.get() == false); ++i) {
                Runnable worker = new RegisterRunnable(seedHosts.get(i), seedPorts.get(i), status, completed);
                executor.execute(worker);
            }

            while(!completed.get()) {}
            executor.shutdown();
        }
        return status.get();
    }

    static class RegisterRunnable implements Runnable {

        private final int seedPort;
        private final String seedHost;
        public AtomicBoolean status;
        public AtomicBoolean completed;

        public RegisterRunnable(String seedHost, int seedPort, AtomicBoolean status, AtomicBoolean completed){
            this.seedHost = seedHost;
            this.seedPort = seedPort;
            this.status = status;
            this.completed = completed;
        }

        public void run() {
            try {
                TTransport transport = new TSocket(seedHost, seedPort);
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);

                A1Management.Client client = new A1Management.Client(protocol);
                status.set(client.registerNode(description));
                completed.set(true);
                transport.close();

            } catch(TException te) {
                te.printStackTrace();
            }
        }
    }

    protected boolean isSeedNode() {
        boolean isSeed = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            if (seedHosts.get(i).equals(description.host) && seedPorts.get(i).equals(description.mport)) {
                isSeed = true;
            }
        }

        return isSeed;
    }

}
