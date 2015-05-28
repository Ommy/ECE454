package handlers;

import ece454750s15a1.A1Management;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerData;
import org.apache.thrift.TException;
import servers.IServer;
import services.IManagementServiceRequest;

import java.util.Arrays;
import java.util.List;

public class ManagementHandler extends BaseHandler implements A1Management.Iface {

    protected ManagementHandler(IServer server) {
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
    public ServerData exchangeServerData(final ServerData data) throws TException {
        return executor.requestExecute(new IManagementServiceRequest() {
            @Override
            public ServerData perform(A1Management.Iface client) throws TException {
                return client.exchangeServerData(data);
            }
        });
    }
}