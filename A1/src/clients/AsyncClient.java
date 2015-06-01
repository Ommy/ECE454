package clients;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
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

        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            TAsyncClientManager clientManager = new TAsyncClientManager();
            TNonblockingTransport transport = new TNonblockingSocket(description.getHost(), description.getPport());

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
