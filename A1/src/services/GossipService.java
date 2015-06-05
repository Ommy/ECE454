package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerData;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requests.IManagementServiceRequest;
import servers.IServer;

import java.util.Set;

public class GossipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossipService.class.getName());

    private final IServer server;

    public GossipService(IServer server) {
        this.server = server;
    }

    public void gossip() throws ServiceUnavailableException {
        final ServerData myData = server.getData();

        ServerData theirData = server.getServiceExecutor().requestExecuteAnyByType(ServerType.FE, new IManagementServiceRequest()
        {
            @Override
            public ServerData perform(A1Management.Iface client) throws TException {
                return client.exchangeServerData(myData);
            }
        });

        if (theirData != null) {
            server.updateData(theirData);
        } else {
            LOGGER.error("Unexpected error: failed to gossip");
        }
    }
}
