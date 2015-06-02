package clients;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.List;

public class JavaBEClient extends BaseClient {
    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            TTransport transport = new TSocket(description.getHost(), description.getMport());

            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);

            A1Management.Client client = new A1Management.Client(protocol);

            List<String> pass = client.getGroupMembers();

            System.out.println("members: " + pass.toString());

            transport.close();

        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
