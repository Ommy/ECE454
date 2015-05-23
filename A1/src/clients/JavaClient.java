package clients;
import ece454750s15a1.*;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

public class JavaClient {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please enter 'simple' or 'secure'");
            System.exit(0);
        }
        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);

            perform(client);
        } catch (TException x) {
            x.printStackTrace();
        } finally {
            transport.close();
        }
    }

    public static void perform(A1Password.Client client) throws TException {
        String pass = client.hashPassword("hunter2", (short)10);
        System.out.println(pass);
    }
}