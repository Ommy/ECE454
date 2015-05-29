package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import servers.IServer;

public class ServiceExecutor {
    private final IScheduler scheduler;
    private final ClientPoolService clients;

    public ServiceExecutor(IServer server, IScheduler scheduler) {
        this.scheduler = scheduler;

        clients = new ClientPoolService(server);
    }

    // Only use for registering with seed nodes when you have no ServerDescription data
    public <T> T requestExecuteToServer(String host, int mport, IManagementServiceRequest request) {
        return clients.callOnce(host, mport, request);
    }

    // Implementations for Management and Password requests
    private <T> T requestExecuteToServer(ServerDescription server, IManagementServiceRequest request) {
        return clients.call(server, request);
    }

    private <T> T requestExecuteToServer(ServerDescription server, IPasswordServiceRequest request) {
        return clients.call(server, request);
    }

    public <T> T requestExecute(ServerType type, IManagementServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = scheduler.getNextServerByType(type);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecute(ServerType type, IPasswordServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = scheduler.getNextServerByType(type);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecuteAny(IManagementServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = scheduler.getNextServer();
        return requestExecuteToServer(server, request);
    }
}

