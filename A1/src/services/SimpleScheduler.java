package services;

import ece454750s15a1.ServerDescription;
import servers.IServer;

import java.util.Random;

public class SimpleScheduler implements IScheduler {
    private IServer server;

    public SimpleScheduler(IServer server) {
        this.server = server;
    }

    @Override
    public ServerDescription next() {
        Random random = new Random();
        int r = random.nextInt(server.getData().getOnlineServersSize());
        return server.getData().getOnlineServers().get(r);
    }
}
