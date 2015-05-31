package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerType;
import org.apache.thrift.TException;
import servers.IServer;
import requests.IPasswordServiceRequest;

public class FPasswordHandler extends BaseHandler implements A1Password.Iface {

    public FPasswordHandler(IServer server, PerfCounters counter) {
        super(server);
        setPerfCounter(counter);
    }

    @Override
    public String hashPassword(final String password, final short logRounds) throws TException {
        System.out.println("FE Server received request to hashPassword");

        updateRequestsReceived();

        String hashedPassword =  server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public String perform(A1Password.Iface client) throws TException {
                System.out.println("Calling hashPassword on client");
                return client.hashPassword(password, logRounds);
            }
        });

        updateRequestsCompleted();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(final String password, final String hash) throws TException {
        System.out.println("FE Server received request to checkPassword");

        updateRequestsReceived();

        Boolean result = (Boolean)server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public Boolean perform(A1Password.Iface client) throws TException {
                System.out.println("Calling checkPassword on client");
                return client.checkPassword(password, hash);
            }
        });

        updateRequestsCompleted();
        return result;
    }

    private void updateRequestsReceived() {
        counter.setNumRequestsReceived(counter.getNumRequestsReceived() + 1);
    }

    private void updateRequestsCompleted() {
        counter.setNumRequestsCompleted(counter.getNumRequestsCompleted() + 1);
    }
}
