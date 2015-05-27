package servers;

import ece454750s15a1.A1Password;
import handlers.BPasswordHandler;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class BEServer extends servers.Server {
    public static A1Password.Processor processor;

    public static void main(String[] args) {
        initialize(args);

        // try to register this seed node with the others
        System.out.println("Registering...");
        boolean status = false;
        int attempts = 0;
        while (!status && attempts < 10) {
            try {
                status = register();
            } catch (Exception e) {
                e.printStackTrace();
            }

            attempts++;
        }

        try {
            Runnable simple = new Runnable() {
                public void run() {
                    A1Password.Iface handler = new BPasswordHandler();
                    final TProcessor processor = new A1Password.Processor<A1Password.Iface>(handler);

                    try {
                        System.out.println("Starting Backend Server");

                        TServerTransport serverTransport = new TServerSocket(16721);
                        TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
                        server.serve();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            new Thread(simple).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
