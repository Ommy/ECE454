package servers;


import ece454750s15a1.A1Management;
import handlers.ManagementHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class RegistrationServer {

    public static A1Management.Processor processor;

    public static void main(String[] args) {
        try {
            ManagementHandler handler = new ManagementHandler();
            processor = new A1Management.Processor(handler);
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

    private static void simple(A1Management.Processor processor) {
        try {
            System.out.println("Running Registration Server");
            TServerTransport serverTransport = new TServerSocket(16723);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
