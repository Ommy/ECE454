package handlers;

import ece454750s15a1.*;

import org.apache.thrift.TException;

import java.util.List;

public class ManagementHandler implements A1Management.Iface {
    @Override
    public List<String> getGroupMembers() throws TException {
        return null;
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        return null;
    }
}
