package requests;

import ece454750s15a1.A1Management;
import org.apache.thrift.TException;

public interface IManagementServiceAsyncRequest {
    <T> T perform(A1Management.AsyncIface client) throws TException;
}
