package services;

import ece454750s15a1.A1Management;
import org.apache.thrift.TException;

public interface IManagementServiceRequest {
    <T> T perform(A1Management.Iface client) throws TException;
}
