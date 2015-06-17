package handlers;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;
import requests.IPasswordServiceRequest;

import java.util.Calendar;

public class FPasswordHandler extends BaseHandler implements A1Password.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(FPasswordHandler.class.getName());

    public FPasswordHandler(IServer server) {
        super(server);
    }

    @Override
    public String hashPassword(final String password, final short logRounds) throws ServiceUnavailableException {
        LOGGER.debug("FE Server received request to hashPassword");

        updateRequestsReceived();

        long startTime = Calendar.getInstance().getTimeInMillis();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String hashedPassword = null;
        while (hashedPassword == null && ((currentTime - startTime)/1000) < 30) {
            try {
                hashedPassword = myServer.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
                    @Override
                    public String perform(A1Password.Iface client) throws TException {
                        LOGGER.debug("Calling hashPassword on client");
                        return client.hashPassword(password, logRounds);
                    }
                });
            } catch (ServiceUnavailableException e) {
                LOGGER.error("Scheduler could not find any free servers", e);
                hashedPassword = null;
            }

            currentTime = Calendar.getInstance().getTimeInMillis();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (hashedPassword == null || ((currentTime - startTime)/1000) > 30) {
            throw new ServiceUnavailableException("hashPassword request timed out");
        }

        updateRequestsCompleted();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(final String password, final String hash) throws ServiceUnavailableException {
        LOGGER.debug("FE Server received request to checkPassword");

        updateRequestsReceived();

        long startTime = Calendar.getInstance().getTimeInMillis();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Boolean result = null;
        while (result == null && ((currentTime - startTime)/1000) < 30) {
            try {
                result = myServer.getServiceExecutor().requestExecute(ServerType.BE, new IPasswordServiceRequest() {
                    @Override
                    public Boolean perform(A1Password.Iface client) throws TException {
                        LOGGER.debug("Calling checkPassword on client");
                        return client.checkPassword(password, hash);
                    }
                });
            } catch (ServiceUnavailableException e) {
                LOGGER.error("Server request failed for unknown reasons", e);
                result = null;
            }

            currentTime = Calendar.getInstance().getTimeInMillis();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (result == null || ((currentTime - startTime)/1000) > 30) {
            throw new ServiceUnavailableException("checkPassword request timed out");
        }

        updateRequestsCompleted();
        return result;
    }
}
