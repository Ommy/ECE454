package handlers;

import ece454750s15a1.A1Password;
import org.mindrot.jbcrypt.BCrypt;
import servers.IServer;

public class BPasswordHandler extends BaseHandler implements A1Password.Iface {

    public BPasswordHandler(IServer server) {
        super(server);
    }

    @Override
    public String hashPassword(String password, short logRounds) {
        return BCrypt.hashpw(password, BCrypt.gensalt((int)logRounds));
    }

    @Override
    public boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}