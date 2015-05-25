package servers;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import handlers.FPasswordHandler;
import handlers.ManagementHandler;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FEServer extends servers.Server {

    public static A1Password.Processor pProcessor;
    public static A1Management.Processor mProcessor;

    public static void main(String[] args) {
        initialize(args);

        // try to register this seed node with the others
        boolean status = false;
        int attempts = 0;
        while (!status && attempts < 10) {
            try{
                status = register();
            } catch (Exception e) {
                e.printStackTrace();
            }

            attempts++;
        }

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            FPasswordHandler fHandler = new FPasswordHandler();
            ManagementHandler mHandler = new ManagementHandler();
            mProcessor = new A1Management.Processor(mHandler);
            pProcessor = new A1Password.Processor(fHandler);
            Runnable managementRunnable = new Runnable() {
                public void run() {
                    managementProcess(mProcessor);
                }
            };
            Runnable passwordRunnable = new Runnable() {
                public void run() {
                    passwordProcess(pProcessor);
                }
            };
            executor.execute(managementRunnable);
            executor.execute(passwordRunnable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void managementProcess(A1Management.Processor processor) {
        try {
            System.out.println("Starting Management Server");

            TServerTransport serverTransport = new TServerSocket(description.mport);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void passwordProcess(A1Password.Processor processor) {
        try {
            System.out.println("Starting Password Server");

            TServerTransport serverTransport = new TServerSocket(description.pport);
            TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
