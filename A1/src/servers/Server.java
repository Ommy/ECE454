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

import java.lang.Override;
import java.lang.Runnable;
import java.util.*;
import java.util.concurrent.Executors;

public abstract class Server {
    public static ServerDescription description;
    public static List<String> seedHosts;
    public static List<Integer> seedPorts;

    public Server() {
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();
    }

    public static void initialize(String[] args) {
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
    }

    public static boolean register() throws TException {
        boolean status = false;

        if (!isSeedNode()) {

            ExecutorService executor = Executors.newFixedThreadPool(seedHosts.size());

            for (int i = 0; (i < seedHosts.size()) && (status == false); ++i) {

                Runnable worker = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TTransport transport = new TSocket(seedHosts.get(i), seedPorts.get(i));
                            transport.open();

                            TProtocol protocol = new TBinaryProtocol(transport);
                            A1Management.Client client = new A1Management.Client(protocol);
                            status = client.registerNode(description);
                            transport.close();

                        } catch (TTransportException e) {
                            e.printStackTrace();
                        }
                    }
                };

                executor.execute(worker);

            }
        }

        return status;
    }

    protected static boolean isSeedNode() {

        boolean isSeed = false;
        for (int i = 0; i < seedHosts.size(); ++i) {
            if (seedHosts.get(i).equals(description.host) && seedPorts.get(i).equals(description.mport)) {
                isSeed = true;
            }
        }

        return isSeed;
    }

}
