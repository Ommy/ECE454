package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.mindrot.jbcrypt.BCrypt;

public class FPasswordHandler implements A1Password.Iface {

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        TTransport transport = new TSocket("localhost", 16720);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        A1Password.Client client = new A1Password.Client(protocol);
        String hashedPassword = client.hashPassword(password, logRounds);
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws TException {
        boolean result = BCrypt.checkpw(password, hash);
        return result;
    }
}
