package handlers;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;
import servers.IServer;
import services.IPasswordServiceRequest;

public class FPasswordHandler extends BaseHandler implements A1Password.Iface {

    public FPasswordHandler(IServer server) {
        super(server);
    }

    @Override
    public String hashPassword(final String password, final short logRounds) throws TException {
        return executor.requestExecute(new IPasswordServiceRequest() {
            @Override
            public String perform(A1Password.Iface client) throws TException {
                return client.hashPassword(password, logRounds);
            }
        });
    }

    @Override
    public boolean checkPassword(final String password, final String hash) throws TException {
        return executor.requestExecute(new IPasswordServiceRequest() {
            @Override
            public Boolean perform(A1Password.Iface client) throws TException {
                return client.checkPassword(password, hash);
            }
        });
    }
}
