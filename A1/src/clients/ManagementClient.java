package clients;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManagementClient extends BaseClient {

    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            TTransport transport = new TSocket(description.getHost(), description.getMport());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Management.Client client = new A1Management.Client(protocol);
            System.out.print(client.getGroupMembers().toString());
            System.out.print(client.getPerfCounters().toString());
            System.out.print(client.getServerData().toString());
            System.out.print(client.getServerDescription().toString());
            transport.close();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
