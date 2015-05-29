package services;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import servers.IServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleScheduler implements IScheduler {
    private IServer server;

    public SimpleScheduler(IServer server) {
        this.server = server;
    }

    @Override
    public ServerDescription next(final ServerType type) throws ServiceUnavailableException {
        List<ServerDescription> onlineServers = new ArrayList<ServerDescription>();
        for (ServerDescription description: server.getData().getOnlineServers()) {
            if (description.getType() == type) {
                onlineServers.add(description);
            }
        }

        if (onlineServers.size() == 0) {
            throw new ServiceUnavailableException("Could not find any available servers");
        }

        Random random = new Random();
        int r = random.nextInt(onlineServers.size());
        return onlineServers.get(r);
    }

    @Override
    public ServerDescription nextAny() throws ServiceUnavailableException {
        List<ServerDescription> onlineServers = new ArrayList<ServerDescription>(server.getData().getOnlineServers());

        if (onlineServers.size() == 0) {
            throw new ServiceUnavailableException("Could not find any available servers");
        }

        Random random = new Random();
        int r = random.nextInt(onlineServers.size());
        return onlineServers.get(r);
    }


}
