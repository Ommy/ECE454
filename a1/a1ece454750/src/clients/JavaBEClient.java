package clients;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaBEClient extends BaseClient {
    public static void main(String[] args) {
        final ServerDescription description = parser.parse(args, ServerType.FE);

        List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for (int i = 0; i < 45; ++i) {
            Callable<Void> worker = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        TTransport transport = new TSocket(description.getHost(), description.getPport());
                        transport.open();

                        TProtocol protocol = new TBinaryProtocol(transport);
                        A1Password.Client client = new A1Password.Client(protocol);
                        String pass = client.hashPassword("hunter2", (short) 10);
                        System.out.println("Pass:" + pass);
                        transport.close();
                    } catch (TException x) {
                        x.printStackTrace();
                    }
                    return null;
                }
            };
            workers.add(worker);
        }

        try {
            executorService.invokeAll(workers);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            TTransport transport = new TSocket(description.getHost(), description.getMport());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1Management.Client client = new A1Management.Client(protocol);
            System.out.print(client.getPerfCounters().toString());
            transport.close();

        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }


    }
}
