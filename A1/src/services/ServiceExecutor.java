package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import servers.IServer;

public class ServiceExecutor {
    private final IScheduler randomScheduler;
    private final IScheduler loadBalancedScheduler;
    private final IClientService clientService;

    public ServiceExecutor(IServer server) {
        this.randomScheduler = new RandomScheduler(server);
        this.loadBalancedScheduler = new RandomWeightedCoresScheduler(server);

        clientService = new ClientPoolService(server);
    }

    // Only use for registering with seed nodes when you have no ServerDescription data
    public <T> T requestExecuteToServer(String host, int mport, IManagementServiceRequest request) {
        return clientService.callOnce(host, mport, request);
    }

    // Implementations for Management and Password requests
    private <T> T requestExecuteToServer(ServerDescription server, IManagementServiceRequest request) {
        return clientService.call(server, request);
    }

    private <T> T requestExecuteToServer(ServerDescription server, IPasswordServiceRequest request) {
        return clientService.call(server, request);
    }

    public <T> T requestExecute(ServerType type, IManagementServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = loadBalancedScheduler.getNextServerByType(type);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecute(ServerType type, IPasswordServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = loadBalancedScheduler.getNextServerByType(type);
        return requestExecuteToServer(server, request);
    }

    public <T> T requestExecuteAny(IManagementServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = randomScheduler.getNextServer();
        return requestExecuteToServer(server, request);
    }
}

