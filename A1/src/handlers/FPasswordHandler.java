package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerType;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;
import requests.IPasswordServiceRequest;

public class FPasswordHandler extends BaseHandler implements A1Password.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(FPasswordHandler.class.getName());

    public FPasswordHandler(IServer server, PerfCounters counter) {
        super(server);
        setPerfCounter(counter);
    }

    @Override
    public String hashPassword(final String password, final short logRounds) throws TException {
        LOGGER.debug("FE Server received request to hashPassword");

        updateRequestsReceived();

        String hashedPassword =  server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public String perform(A1Password.Iface client) throws TException {
                LOGGER.debug("Calling hashPassword on client");
                return client.hashPassword(password, logRounds);
            }
        });

        updateRequestsCompleted();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(final String password, final String hash) throws TException {
        LOGGER.debug("FE Server received request to checkPassword");

        updateRequestsReceived();

        Boolean result = server.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
            @Override
            public Boolean perform(A1Password.Iface client) throws TException {
                LOGGER.debug("Calling checkPassword on client");
                return client.checkPassword(password, hash);
            }
        });

        updateRequestsCompleted();
        return result;
    }
}
