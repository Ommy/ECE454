package handlers;

import ece454750s15a1.*;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class ManagementHandler implements A1Management.Iface {

    List<ServerDescription> servers;

    public ManagementHandler() {
        servers = new ArrayList<ServerDescription>();
    }

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
    public boolean registerNode(ServerDescription description) throws TException {
        if (servers.contains(description)){
            return false;
        } else {
            servers.add(description);
            System.out.println("Successfully registered. Server size is: " + servers.size());
            return true;
        }
    }

    @Override
    public boolean announceNode(ServerDescription description) throws TException {
        System.out.println("C");
        return false;
    }
}
