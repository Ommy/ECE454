package clients;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaClient {

    public static void main(String[] args) {

        String host = "localhost";
        int pport = 6719;
        int mport = 4848;
        int ncores = 1;

        List<String> seedsList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && (i+1 < args.length)) {
                host = args[i+1];
            } else if (args[i].equals("-pport") && (i+1 < args.length)) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-mport") && (i+1 < args.length)) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-ncores") && (i+1 < args.length)) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-seeds") && (i+1 < args.length)) {
                seedsList = Arrays.asList(args[i + 1].split(","));
            }
        }

        try {
            TTransport transport = new TSocket(host, pport);
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
