package servers;

import ece454750s15a1.A1Password;
import handlers.FPasswordHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.*;

public class FEServer extends servers.Server {

    public static A1Password.Processor processor;

    public static void main(String[] args) {
        initialize(args);

        try {
            FPasswordHandler handler = new FPasswordHandler();
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
            System.out.println("Starting Backend Server");

            TServerTransport serverTransport = new TServerSocket(16719);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}