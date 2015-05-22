package handlers;

import ece454750s15a1.A1Password;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

public class PasswordAsyncHandler implements A1Password.AsyncIface {

    @Override
    public void hashPassword(String password, short logRounds, AsyncMethodCallback resultHandler) throws TException {

    }

    @Override
    public void checkPassword(String password, String hash, AsyncMethodCallback resultHandler) throws TException {

    }
}
