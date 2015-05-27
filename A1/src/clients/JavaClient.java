package clients;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class JavaClient {

    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 16721);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);

            perform(client);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    public static void perform(A1Password.Client client) throws TException {
        String pass = client.hashPassword("hunter2", (short) 10);
        System.out.println(pass);
    }
}