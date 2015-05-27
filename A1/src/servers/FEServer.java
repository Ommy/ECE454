package servers;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerDescription;
import handlers.FPasswordHandler;
import handlers.ManagementHandler;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FEServer extends servers.Server {

    private final List<ServerDescription> descriptions;
    public A1Password.Processor pProcessor;
    public A1Management.Processor mProcessor;

    public static void main(String[] args) {
        FEServer server = new FEServer();
        server.run(args);
    }

    public FEServer() {
        descriptions = new LinkedList<ServerDescription>();
    }

    public void run(String[] args) {
        initialize(args);

        // try to register this node with the others
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

    public void managementProcess(A1Management.Processor processor) {
        try {

            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getMport());
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.processor(processor);
            args.selectorThreads(8*description.getNcores());
            args.workerThreads(8*description.getNcores());
            TServer server = new TThreadedSelectorServer(args);
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void passwordProcess(A1Password.Processor processor) {
        try {

            TNonblockingServerTransport transport = new TNonblockingServerSocket(description.getPport());
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport);

            args.transportFactory(new TFramedTransport.Factory());
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.processor(processor);
            args.selectorThreads(8*description.getNcores());
            args.workerThreads(8*description.getNcores());
            TServer server = new TThreadedSelectorServer(args);
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FEServerManagementHandler implements A1Management.Iface {

        public FEServerManagementHandler() {
        }

        @Override
        public List<String> getGroupMembers() throws TException {
            System.out.println("A");
            return null;
        }

        @Override
        public PerfCounters getPerfCounters() throws TException {
            System.out.println("B");
            return null;
        }

        @Override
        public boolean registerNode(ServerDescription description) throws TException {
            if (descriptions.contains(description)) {
                return false;
            } else {
                descriptions.add(description);
                System.out.println("Successfully registered. Server size is: " + descriptions.size());
                return true;
            }
        }

        @Override
        public boolean announceNode(ServerDescription description) throws TException {
            if (descriptions.contains(description)) {
                descriptions.get(descriptions.indexOf(description)).setStatus(description.status);
            } else {
                descriptions.add(description);
            }
        }
    }


    public class FPasswordHandler implements A1Password.Iface {

        private final ServerDescription description;

        public FPasswordHandler(ServerDescription description) {
            this.description = description;
        }

        @Override
        public String hashPassword(String password, short logRounds) throws TException {


            TTransport transport = new TSocket("localhost", description.getPport());
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);


            // TODO Victor: Get updated list of backend nodes, and do load
            // balancing here;
            String hashedPassword = client.hashPassword(password, logRounds);
            return hashedPassword;
        }

        @Override
        public boolean checkPassword(String password, String hash) throws TException {
            boolean result = BCrypt.checkpw(password, hash);
            return result;
        }
    }


}

