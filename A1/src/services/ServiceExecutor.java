package services;

import ece454750s15a1.ServerDescription;
import servers.IServer;

public class ServiceExecutor {
    private final IServer server;
    private final IScheduler scheduler;
    private final ClientPoolService clients;

    public ServiceExecutor(IServer server, IScheduler scheduler) {
        this.server = server;
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

    public <T> T requestExecute(IManagementServiceRequest request) {
        ServerDescription server = scheduler.next();
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecute(IPasswordServiceRequest request) {
        ServerDescription server = scheduler.next();
        return requestExecuteToServer(server, request);
    }

}
