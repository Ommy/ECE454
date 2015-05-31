package handlers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import servers.IServer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ManagementHandler extends BaseHandler implements A1Management.Iface {

    public ManagementHandler(IServer server, PerfCounters counter) {
        super(server);
        setPerfCounter(counter);
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        return Arrays.asList("v6lai", "faawan");
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        System.out.println("Getting performance counters");
        long currentTime = Calendar.getInstance().getTimeInMillis();
        counter.setNumSecondUp((int) (currentTime - serverStartTime));
        return counter;
    }

    @Override
    public ServerData exchangeServerData(final ServerData theirData) throws TException {
        System.out.println("Hit exchangeServerData");
        server.updateData(theirData);
        return server.getData();
    }
}