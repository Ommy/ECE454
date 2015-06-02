package clients;

import ece454750s15a1.*;
import org.apache.thrift.TException;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JavaFEClient extends BaseClient {

    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);

            List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
            final Random r = new Random();


            for (int i = 0; i < 10; i++) {
                final int count = i;
                workers.add(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        TTransport transport = new TSocket(description.getHost(), 14560 + count);
                        transport.open();
                        TProtocol protocol = new TBinaryProtocol(transport);
                        A1Password.Client client = new A1Password.Client(protocol);
                        for (int j = 0; j < 10; j++) {
                            System.out.println(client.hashPassword("hunter2" + j, (short) (10 + r.nextInt(4))));
                        }
                        transport.close();
                        return null;
                    }
                });
            }

            executor.invokeAll(workers);

            System.out.println("Finished running invokeAll");

            TTransport transport = null;
            for (int i = 0; i < 10; i++) {
                transport = new TSocket(description.getHost(), 1331+i);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client1 = new A1Management.Client(protocol);
                System.out.println(client1.getPerfCounters().toString());
                transport.close();
            }

         executor.shutdown();

        } catch (TException x) {
            x.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
