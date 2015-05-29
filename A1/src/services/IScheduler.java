package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;

public interface IScheduler {
    ServerDescription next(ServerType type) throws ServiceUnavailableException;

    ServerDescription nextAny() throws ServiceUnavailableException;
}
