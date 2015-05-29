package handlers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import servers.IServer;
import services.IManagementServiceRequest;

import java.util.Arrays;
import java.util.List;

public class ManagementHandler extends BaseHandler implements A1Management.Iface {

    public ManagementHandler(IServer server) {
        super(server);
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        return Arrays.asList("v6lai", "faawan");
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        System.out.println("B");
        return null;
    }

    @Override
    public ServerData exchangeServerData(final ServerData theirData) throws TException {
        System.out.println("Hit exchangeServerData");
        server.updateData(theirData);
        return server.getData();
    }
//
//        // Determine how to send this to the right client
//        return executor.requestExecute(new IManagementServiceRequest() {
//            @Override
//            public ServerData perform(A1Management.Iface client) throws TException {
//                System.out.println("Exchanging data..." + ourData.toString());
//                ServerData theirData = client.exchangeServerData(ourData);
//                System.out.println("Completed exchange");
//                return theirData;
//            }
//        });
//    }
}