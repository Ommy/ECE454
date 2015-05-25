package handlers;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

public class ManagementAsyncHandler implements A1Management.AsyncIface {
    @Override
    public void getGroupMembers(AsyncMethodCallback resultHandler) throws TException {

    }

    @Override
    public void getPerfCounters(AsyncMethodCallback resultHandler) throws TException {

    }

    @Override
    public void registerNode(ServerDescription description, AsyncMethodCallback resultHandler) throws TException {

    }

    @Override
    public void announceNode(ServerDescription description, AsyncMethodCallback resultHandler) throws TException {

    }
}
