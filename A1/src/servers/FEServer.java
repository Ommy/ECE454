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


    public static void main(String[] args) {
        FEServer server = new FEServer(args);
        server.run();
    }

    public FEServer(String[] args) {
        super(args);
    }

    public void run() {
        onStartupRegister();

        final A1Password.Iface fHandler = new FEServerPasswordHandler();
        final A1Management.Iface mHandler = new FEServerManagementHandler();

        final A1Password.Processor fProcessor;


        onStartupInitializeServices(fHandler, mHandler);
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


    public class FEServerPasswordHandler implements A1Password.Iface {

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

