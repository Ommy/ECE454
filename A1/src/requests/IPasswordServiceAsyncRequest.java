package requests;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;

public interface IPasswordServiceAsyncRequest {
    <T> T perform(A1Password.AsyncIface client) throws TException;
}
