package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;

public class PasswordHandler implements A1Password.Iface {

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        return null;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws TException {
        return false;
    }
}
