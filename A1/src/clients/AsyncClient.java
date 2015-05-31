package clients;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncClient extends BaseClient {

    static volatile boolean finish = false;
    public static void main(String [] args) {

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
            final String outHost = host;
            final int outPport = pport;
            final int outMport = mport;
            final int outNcores = ncores;
            final List<String> outSeedsList = seedsList;

            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TNonblockingTransport transport = new TNonblockingSocket(outHost, outPport);

            A1Password.AsyncClient client = new A1Password.AsyncClient(
                    protocolFactory, clientManager, transport);

            client.hashPassword("abc", (short)5, new HashPasswordCallback());

            System.out.println("After Send Async call.");

            int i = 0;
            while (!finish) {
                try {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e) {
                    System.out.println(e);
                }
                i++;
                System.out.println("Sleep " + i + " Seconds.");
            }

            System.out.println("Exiting client.");

        } catch (TException x) {
            x.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class HashPasswordCallback
            implements AsyncMethodCallback<A1Password.AsyncClient.hashPassword_call> {

        public void onComplete(A1Password.AsyncClient.hashPassword_call hashPassword_call) {
            try {
                String result = hashPassword_call.getResult();
                System.out.println("Add from server: " + result);
            } catch (TException e) {
                e.printStackTrace();
            }
            finish = true;
        }

        public void onError(Exception e) {
            System.out.println("Error : ");
            e.printStackTrace();
            finish = true;
        }
    }

}
