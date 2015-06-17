package schedulers;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomWeightedCoresScheduler implements IScheduler{

    private final IServer myServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomWeightedCoresScheduler.class.toString());

    public RandomWeightedCoresScheduler(IServer server) {
        myServer = server;
    }

    @Override
    public ServerDescription getNextServerByType(final ServerType type) throws ServiceUnavailableException {
        List<ServerDescription> onlineNodes = new LinkedList<ServerDescription>();
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
            if (weightedRandom < description.getNcores()) {
                selectedServer = description;
                break;
            }
            weightedRandom -= description.getNcores();
        }

        return selectedServer;
    }

    @Override
    public ServerDescription getNextServer() throws ServiceUnavailableException {
        List<ServerDescription> onlineNodes = new LinkedList<ServerDescription>();
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
            if (weightedRandom < description.getNcores()) {
                selectedServer = description;
                break;
            }
            weightedRandom -= description.getNcores();
        }

        return selectedServer;
    }

}
