package schedulers;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import servers.IServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomScheduler implements IScheduler {
    private final IServer myServer;

    public RandomScheduler(IServer server) {
        myServer = server;
    }

    @Override
    public ServerDescription getNextServerByType(final ServerType type) throws ServiceUnavailableException {
        List<ServerDescription> onlineServers = new ArrayList<ServerDescription>(myServer.getData().getOnlineServers());
        List<ServerDescription> availableServers = new ArrayList<ServerDescription>();
        for (ServerDescription description: onlineServers) {
            if (description.getType() == type) {
                availableServers.add(description);
            }
        }

        if (availableServers.contains(myServer.getDescription())) {
            availableServers.remove(myServer.getDescription());
        }

        if (availableServers.size() == 0) {
            throw new ServiceUnavailableException("Could not find any available servers");
        }

        Random random = new Random();
        int r = random.nextInt(availableServers.size());
        return availableServers.get(r);
    }

    @Override
    public ServerDescription getNextServer() throws ServiceUnavailableException {
        List<ServerDescription> onlineServers = new ArrayList<ServerDescription>(myServer.getData().getOnlineServers());

        if (onlineServers.contains(myServer.getDescription())) {
            onlineServers.remove(myServer.getDescription());
        }

        if (onlineServers.size() == 0) {
            throw new ServiceUnavailableException("Could not find any available servers");
        }

        Random random = new Random();
        int r = random.nextInt(onlineServers.size());
        return onlineServers.get(r);
    }
}
