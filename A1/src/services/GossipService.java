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

public class GossipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossipService.class.getName());

    private IServer server;

    public GossipService(IServer server) {
        this.server = server;
    }

    public void gossip() throws ServiceUnavailableException {
        while (true) {
            try {
                // at least one myServer not myself
                if (server.getData().getOnlineServersSize() > 1) {

                    // gossip protocol handshakes with all online servers
                    ServerData theirData = server.getServiceExecutor().requestExecuteAnyByType(ServerType.FE, new IManagementServiceRequest()
                    {
                        @Override
                        public ServerData perform(A1Management.Iface client) throws TException {
                            return client.exchangeServerData(server.getData());
                        }
                    });

                    if (theirData != null) {
                        server.updateData(theirData);
                    } else {
                        LOGGER.error("Lost gossip");
                    }

                    Thread.sleep(100);
                } else {
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
