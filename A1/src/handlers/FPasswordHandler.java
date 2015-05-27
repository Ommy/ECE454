package handlers;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

public class FPasswordHandler implements A1Password.Iface {

    @Override
    public String hashPassword(String password, short logRounds) throws TException {
//
//        TTransport transport = new TSocket("localhost", description.getPport());
//        transport.open();
//        TProtocol protocol = new TBinaryProtocol(transport);
//        A1Password.Client client = new A1Password.Client(protocol);
//
//        // TODO Victor: Get updated list of backend nodes, and do load
//        // balancing here;
//        String hashedPassword = client.hashPassword(password, logRounds);
        return null;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws TException {
        return BCrypt.checkpw(password, hash);
    }
}
