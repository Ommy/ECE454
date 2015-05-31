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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ParallelClient extends BaseClient{

    static int counter = 5;
    static CountDownLatch latch = new CountDownLatch(counter);

    public static void main(String [] args) {

        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            for(int i = 0; i < 5; ++i){
                System.out.println("Send request i = " + i);
                new Thread() {
                    public void run() {
                        try {
                            TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
                            TAsyncClientManager clientManager = new TAsyncClientManager();
                            TNonblockingTransport transport = new TNonblockingSocket(description.getHost(), description.getPport());
                            A1Password.AsyncClient client = new A1Password.AsyncClient(
                                    protocolFactory, clientManager, transport);

                            client.hashPassword("abc", (short)5, new HashPasswordCallback(latch, transport));
                        } catch (TException x) {
                            x.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                System.out.println("After Send request i = " + i);
            }
            boolean wait = latch.await(30, TimeUnit.SECONDS);
            System.out.println("latch.await =:" + wait);

            System.out.println("Exiting client.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class HashPasswordCallback
            implements AsyncMethodCallback<A1Password.AsyncClient.hashPassword_call> {

        private CountDownLatch latch;
        private TNonblockingTransport transport;

        public HashPasswordCallback(CountDownLatch latch, TNonblockingTransport transp) {
            this.latch = latch;
            this.transport = transp;
        }
        public void onComplete(A1Password.AsyncClient.hashPassword_call hashPassword_call) {
            try {
                String result = hashPassword_call.getResult();
                System.out.println("Add from server: " + result);
            } catch (TException e) {
                e.printStackTrace();
            } finally {
                transport.close();
                latch.countDown();
            }
        }

        public void onError(Exception e) {
            System.out.println("Error : ");
            e.printStackTrace();
            latch.countDown();
        }
    }
}
