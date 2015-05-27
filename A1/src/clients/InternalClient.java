package clients;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import servers.Server;

import java.util.List;

public abstract class InternalClient implements A1Management.Iface, A1Password.Iface {
    private final ServerDescription description;

    protected InternalClient(ServerDescription description) {
        this.description = description;
    }


    @Override
    public List<String> getGroupMembers() throws TException {
        return null;
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        return null;
    }

    @Override
    public boolean announceNode(ServerDescription description) throws TException {
        return false;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws TException {
        return false;
    }
}
