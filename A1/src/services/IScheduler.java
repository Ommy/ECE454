package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;

public interface IScheduler {
    ServerDescription next(ServerType type, ServerDescription description) throws ServiceUnavailableException;

    ServerDescription nextAny(ServerDescription description) throws ServiceUnavailableException;
}
