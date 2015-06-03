package handlers;

import ece454750s15a1.A1Password;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;

public class BPasswordHandler extends BaseHandler implements A1Password.Iface {
    private static final Logger LOGGER = LoggerFactory.getLogger(BPasswordHandler.class.getName());

    public BPasswordHandler(IServer server) {
        super(server);
    }

    @Override
    public String hashPassword(String password, short logRounds) {
        LOGGER.debug("Hash password");
        LOGGER.info("RECEIVED REQUEST");

        updateRequestsReceived();
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt((int)logRounds));
        updateRequestsCompleted();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(String password, String hash) {
        LOGGER.debug("Check password");

        updateRequestsReceived();
        boolean result = BCrypt.checkpw(password, hash);
        updateRequestsCompleted();
        return result;
    }
}
