package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServerType;
import org.apache.thrift.TException;
import servers.IServer;
import services.IPasswordServiceRequest;

public class FPasswordHandler extends BaseHandler implements A1Password.Iface {

    public FPasswordHandler(IServer server) {
        super(server);
    }

    @Override
    public String hashPassword(final String password, final short logRounds) throws TException {
        System.out.println("FE Server received request to hashPassword");
        return server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public String perform(A1Password.Iface client) throws TException {
                System.out.println("Calling hashPassword on client");
                return client.hashPassword(password, logRounds);
            }
        });
    }

    @Override
    public boolean checkPassword(final String password, final String hash) throws TException {
        System.out.println("FE Server received request to checkPassword");
        return (Boolean)server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public Boolean perform(A1Password.Iface client) throws TException {
                System.out.println("Calling checkPassword on client");
                return client.checkPassword(password, hash);
            }
        });
    }
}
