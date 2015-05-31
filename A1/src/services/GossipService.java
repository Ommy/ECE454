package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.ServerData;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import requests.IManagementServiceRequest;
import servers.IServer;

public class GossipService {

    private IServer server;

    public GossipService(IServer server) {
        this.server = server;
    }

    public void gossip() throws ServiceUnavailableException {
        while (true) {
            try {
                if (server.getData().getOnlineServersSize() > 1) {

                    // gossip protocol handshakes will all online servers
                    server.getServiceExecutor().requestExecuteAny(new IManagementServiceRequest() {
                        @Override
                        public Void perform(A1Management.Iface client) throws TException {
                            System.out.println("Begin gossip handshake");

                            ServerData theirData = client.exchangeServerData(server.getData());
                            if (theirData == null) {
                                throw new TException("Connection error");
                            }

                            server.updateData(theirData);

                            System.out.println("End gossip handshake");
                            return null;
                        }
                    });
                    Thread.sleep(100);
                } else {
                    Thread.sleep(250);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}