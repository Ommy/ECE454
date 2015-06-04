package clients;

import ece454750s15a1.*;
import org.apache.thrift.TException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class JavaFEClient extends BaseClient {

    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        try {
            ExecutorService executor = Executors.newFixedThreadPool(10);

            List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
            for (int i = 0; i < 10; i++) {
                final int count = i;
                workers.add(new Callable<Void>() {
                    @Override
                    public Void call() {
                        TTransport transport = new TFramedTransport(new TSocket(description.getHost(), description.getPport()));

                        try {
                            transport.open();
                        } catch (TTransportException e) {
                            e.printStackTrace();
                        }
                        TProtocol protocol = new TCompactProtocol(transport);
                        A1Password.Client client = new A1Password.Client(protocol);
                        for (int j = 0; j < 50; j++) {
                            System.out.println("At index: " + j + " worker: " + count);
                            String hashed = null;
                            try {
                                hashed = client.hashPassword("hunter2" + j, (short) (5));
                            } catch (TException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Hashed: " + hashed);
                        }
                        transport.close();
                        System.out.println("Completed");
                        return null;
                    }
                });
            }

            executor.invokeAll(workers);

            System.out.println("Completed");

            TTransport transport = null;
            for (int i = 0; i < 1; i++) {
                transport = new TSocket(description.getHost(), description.getMport());
                transport.open();
                TProtocol protocol = new TCompactProtocol(transport);
                A1Management.Client client1 = new A1Management.Client(protocol);
                System.out.println(client1.getPerfCounters().toString() + " index: " + i);
                transport.close();
            }

        } catch (TException x) {
            x.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
