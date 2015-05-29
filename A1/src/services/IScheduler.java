package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;

public interface IScheduler {
    ServerDescription getNextServerByType(ServerType type) throws ServiceUnavailableException;

    ServerDescription getNextServer() throws ServiceUnavailableException;
}
