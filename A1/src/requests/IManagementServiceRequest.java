package requests;

import ece454750s15a1.A1Management;
import org.apache.thrift.TException;

import java.net.ConnectException;

public interface IManagementServiceRequest {
    <T> T perform(A1Management.Iface client) throws TException;
}
