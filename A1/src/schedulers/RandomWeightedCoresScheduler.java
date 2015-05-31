package schedulers;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import servers.IServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWeightedCoresScheduler implements IScheduler{

    private final IServer myServer;

    public RandomWeightedCoresScheduler(IServer server) {
        myServer = server;
    }

    @Override
    public ServerDescription getNextServerByType(final ServerType type) throws ServiceUnavailableException {
        List<ServerDescription> onlineNodes = new ArrayList<ServerDescription>();
        int weightSum = 0;

        for (ServerDescription description : myServer.getData().getOnlineServers()) {
            if (description.getType() == type) {
                onlineNodes.add(description);
                weightSum += description.getNcores();
            }
        }

        if (onlineNodes.size() == 0) {
            throw new ServiceUnavailableException(("Could not find any available servers"));
        }

        Random random = new Random();
        int weightedRandom = random.nextInt(weightSum);

        ServerDescription selectedServer = onlineNodes.get(random.nextInt(onlineNodes.size()));
        for (ServerDescription description : onlineNodes) {
            weightedRandom -= description.getNcores();
            if (weightedRandom < description.getNcores()) {
                selectedServer = description;
            }
        }

        return selectedServer;
    }

    @Override
    public ServerDescription getNextServer() throws ServiceUnavailableException {
        List<ServerDescription> onlineNodes = new ArrayList<ServerDescription>();
        int weightSum = 0;

        for (ServerDescription description : myServer.getData().getOnlineServers()) {
            onlineNodes.add(description);
            weightSum += description.getNcores();
        }

        if (onlineNodes.size() == 0) {
            throw new ServiceUnavailableException(("Could not find any available servers"));
        }

        Random random = new Random();
        int weightedRandom = random.nextInt(weightSum);

        ServerDescription selectedServer = onlineNodes.get(random.nextInt(onlineNodes.size()));
        for (ServerDescription description : onlineNodes) {
            weightedRandom -= description.getNcores();
            if (weightedRandom < description.getNcores()) {
                selectedServer = description;
            }
        }

        return selectedServer;
    }

}
