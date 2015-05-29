package services;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;

public interface IPasswordServiceRequest {
    <T> T perform(A1Password.Iface client) throws TException;
}
