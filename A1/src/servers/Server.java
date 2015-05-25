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

import java.util.*;

public abstract class Server {
    public static ServerDescription description;
    public static List<String> seedHosts;
    public static List<Integer> seedPorts;

    public Server() {

    }

    public static void initialize(String[] args) {
        seedHosts = new ArrayList<String>();
        seedPorts = new ArrayList<Integer>();
        String host = "localhost";
        int pport = 9000;
        int mport = 9001;
        int ncores = 2;
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

    public static void register() throws TException {
        try {
            TTransport transport = new TSocket(seedHosts.get(0), seedPorts.get(0));
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Management.Client client = new A1Management.Client(protocol);
            if (client.registerNode(description)) {
                System.out.println("Success!");
                transport.close();
            } else {
                transport.close();
                System.out.println("Failure :(");
            }
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
