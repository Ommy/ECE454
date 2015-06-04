package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;
import schedulers.IScheduler;
import schedulers.RandomScheduler;
import schedulers.RandomWeightedCoresScheduler;
import schedulers.WeightedRoundRobinScheduler;
import servers.IServer;
import services.clientpool.IClientService;
import services.clientpool.ManagementClientPoolService;
import services.clientpool.PasswordClientPoolService;

public class ServiceExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutor.class.getName());

    private final IScheduler randomScheduler;
    private final IScheduler loadBalancedScheduler;
    private final IClientService<IPasswordServiceRequest, IPasswordServiceAsyncRequest> passwordClientService;
    private final IClientService<IManagementServiceRequest, IManagementServiceAsyncRequest> managementClientService;

    public ServiceExecutor(IServer server) {
        this.randomScheduler = new RandomScheduler(server);
        this.loadBalancedScheduler = new RandomWeightedCoresScheduler(server);

        managementClientService = new ManagementClientPoolService(server);
        passwordClientService = new PasswordClientPoolService(server);
    }

    // Only use for registering with seed nodes when you have no ServerDescription data
    public <T> T requestExecuteToServer(String host, int mport, IManagementServiceRequest request) {
        return managementClientService.callOnce(host, mport, request);
    }

    // Implementations for Management and Password requests
    public  <T> T requestExecuteToServer(ServerDescription server, IManagementServiceRequest request) {
        return managementClientService.call(server, request);
    }

    private <T> T requestExecuteToServer(ServerDescription server, IPasswordServiceRequest request) {
        return passwordClientService.call(server, request);
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

    public <T> T requestExecuteAnyByType(ServerType type, IManagementServiceRequest request) throws ServiceUnavailableException {
        ServerDescription server = randomScheduler.getNextServerByType(type);
        return requestExecuteToServer(server, request);
    }
}

