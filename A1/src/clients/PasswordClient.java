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

public class PasswordClient extends BaseClient {

    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            TTransport transport = new TSocket(description.getHost(), description.getPport());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);

            String hash1 = client.hashPassword("helloworld1", (short) 10);
            String hash2 = client.hashPassword("helloworld2", (short)10);
            String hash3 = client.hashPassword("helloworld3", (short)10);
            String hash4 = client.hashPassword("helloworld4", (short)10);
            String hash5 = client.hashPassword("helloworld5", (short)10);
            String hash6 = client.hashPassword("helloworld6", (short)10);
            String hash7 = client.hashPassword("helloworld7", (short)10);
            String hash8 = client.hashPassword("helloworld9", (short)10);
            String hash9 = client.hashPassword("helloworld9", (short)10);
            String hash10 = client.hashPassword("helloworld10", (short)10);

            System.out.print(hash1);
            System.out.print(hash2);
            System.out.print(hash3);
            System.out.print(hash4);
            System.out.print(hash5);
            System.out.print(hash6);
            System.out.print(hash7);
            System.out.print(hash8);
            System.out.print(hash9);
            System.out.print(hash10);

            System.out.print(client.checkPassword(hash1, "helloworld1"));
            System.out.print(client.checkPassword(hash1, "helloworld2"));
            System.out.print(client.checkPassword(hash1, "helloworld3"));
            System.out.print(client.checkPassword(hash1, "helloworld4"));
            System.out.print(client.checkPassword(hash1, "helloworld5"));
            System.out.print(client.checkPassword(hash1, "helloworld6"));
            System.out.print(client.checkPassword(hash1, "helloworld7"));
            System.out.print(client.checkPassword(hash1, "helloworld8"));
            System.out.print(client.checkPassword(hash1, "helloworld9"));
            System.out.print(client.checkPassword(hash1, "helloworld10"));

            transport.close();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
