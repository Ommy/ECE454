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

    public <T> T requestExecuteToServer(String host, int mport, IManagementServiceRequest request) {
        return clients.callOnce(host, mport, request);
    }

    public <T> T requestExecuteToServer(ServerDescription server, IManagementServiceRequest request) {
        return clients.call(server, request);
    }

    public <T> T requestExecuteToServer(ServerDescription server, IPasswordServiceRequest request) {
        return clients.call(server, request);
    }

    public <T> T requestExecute(ServerType type, IManagementServiceRequest request, ServerDescription description) throws ServiceUnavailableException {
        ServerDescription server = scheduler.next(type, description);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecute(ServerType type, IPasswordServiceRequest request, ServerDescription description) throws ServiceUnavailableException {
        ServerDescription server = scheduler.next(type, description);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecute(IManagementServiceRequest request, ServerDescription description) throws ServiceUnavailableException {
        ServerDescription server = scheduler.nextAny(description);
        return requestExecuteToServer(server, request);
    }
}

