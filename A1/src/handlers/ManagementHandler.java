package handlers;

import ece454750s15a1.A1Management;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerData;
import org.apache.thrift.TException;
import java.util.List;

public class ManagementHandler implements A1Management.Iface {

    @Override
    public List<String> getGroupMembers() throws TException {
        System.out.println("A");
        return null;
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        System.out.println("B");
        return null;
    }

    @Override
    public ServerData exchangeServerData(ServerData data) throws TException {
        return null;
    }
}