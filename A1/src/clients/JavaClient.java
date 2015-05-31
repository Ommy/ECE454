package clients;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import org.apache.thrift.TException;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaClient extends BaseClient {

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
        } catch (TException x) {
            x.printStackTrace();
        }
    }

}
