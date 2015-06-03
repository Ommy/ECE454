package handlers;

import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ManagementHandler extends BaseHandler implements A1Management.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementHandler.class.getName());

    private static final List<String> GROUP_MEMBERS = Arrays.asList("v6lai", "faawan");

    public ManagementHandler(IServer server) {
        super(server);
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        return new ArrayList<String>(GROUP_MEMBERS);
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        LOGGER.debug("Getting performance counters");
        long currentTime = Calendar.getInstance().getTimeInMillis();
        counter.setNumSecondUp((int)(currentTime - serverStartTime) / 1000);
        return counter;
    }

    @Override
    public ServerData exchangeServerData(final ServerData theirData) throws TException {
        LOGGER.debug("Hit exchangeServerData");

        if (theirData != null) {
            myServer.updateData(theirData);
        }

        return myServer.getData();
    }

    @Override
    public void serviceEndpointDown(ServerDescription server) throws TException {
        LOGGER.debug("Server " + server.toString() + " is down");
        myServer.removeDownedService(server);
    }

    @Override
    public ServerData getServerData() throws TException {
        return myServer.getData();
    }

    @Override
    public ServerDescription getServerDescription() throws TException {
        return myServer.getDescription();
    }
}
