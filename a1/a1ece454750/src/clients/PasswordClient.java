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

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);

            transport.open();

            String hash1 = client.hashPassword("helloworld1", (short) 10);
            String hash2 = client.hashPassword("helloworld2", (short)10);
            String hash3 = client.hashPassword("helloworld3", (short)10);
            String hash4 = client.hashPassword("helloworld4", (short)10);
            String hash5 = client.hashPassword("helloworld5", (short)10);
            String hash6 = client.hashPassword("helloworld6", (short)10);
            String hash7 = client.hashPassword("helloworld7", (short)10);
            String hash8 = client.hashPassword("helloworld8", (short)10);
            String hash9 = client.hashPassword("helloworld9", (short)10);
            String hash10 = client.hashPassword("helloworld10", (short)10);

            System.out.println(hash1);
            System.out.println(hash2);
            System.out.println(hash3);
            System.out.println(hash4);
            System.out.println(hash5);
            System.out.println(hash6);
            System.out.println(hash7);
            System.out.println(hash8);
            System.out.println(hash9);
            System.out.println(hash10);

            System.out.println(client.checkPassword("helloworld1", hash1));
            System.out.println(client.checkPassword("helloworld2", hash2));
            System.out.println(client.checkPassword("helloworld3", hash3));
            System.out.println(client.checkPassword("helloworld4", hash4));
            System.out.println(client.checkPassword("helloworld5", hash5));
            System.out.println(client.checkPassword("helloworld6", hash6));
            System.out.println(client.checkPassword("helloworld7", hash7));
            System.out.println(client.checkPassword("helloworld8", hash8));
            System.out.println(client.checkPassword("helloworld9", hash9));
            System.out.println(client.checkPassword("helloworld10", hash10));

            transport.close();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
