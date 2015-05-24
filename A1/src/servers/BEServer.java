package servers;

import ece454750s15a1.A1Password;
import handlers.BPasswordHandler;
import handlers.FPasswordHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.*;

public class BEServer {
    public static A1Password.Processor processor;

    public static void main(String[] args) {
        String host;
        List<String> seedsList = new ArrayList<String>();
        int pport, mport, ncores;
        Map<String, Integer> seeds = new HashMap<String, Integer>();

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

        // Generate seed map
        for(String seed: seedsList) {
            String[] splitSeed = seed.split(":");
            seeds.put(splitSeed[0], Integer.parseInt(splitSeed[1]));
        }

        try {
            BPasswordHandler handler = new BPasswordHandler();
            processor = new A1Password.Processor(handler);
            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void simple(A1Password.Processor processor) {

        try {
            System.out.println("Starting Simple Server");

            TServerTransport serverTransport = new TServerSocket(16720);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
