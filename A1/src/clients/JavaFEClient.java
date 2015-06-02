package clients;

import ece454750s15a1.*;
import org.apache.thrift.TException;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;


public class JavaFEClient extends BaseClient {

    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            TTransport transport = new TSocket(description.getHost(), description.getPport());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);
            String pass = client.hashPassword("hunter2", (short) 10);

            System.out.println("pass: " + pass);

            transport.close();

            transport = new TSocket(description.getHost(), description.getMport());
            transport.open();

            protocol = new TBinaryProtocol(transport);
            A1Management.Client client1 = new A1Management.Client(protocol);
            PerfCounters counter = client1.getPerfCounters();
            System.out.println(counter.toString());
            transport.close();

        } catch (TException x) {
            x.printStackTrace();
        }
    }

}
