package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordHandler implements A1Password.Iface {

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws TException {
        boolean result = BCrypt.checkpw(password, hash);
        return result;
    }
}
