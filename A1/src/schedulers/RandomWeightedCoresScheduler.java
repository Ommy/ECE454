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
        List<ServerDescription> onlineTypedNodes = new ArrayList<ServerDescription>();
        int weightedSum = 0;

        if (myServer.getData().getOnlineServersSize() == 0) {
            throw new ServiceUnavailableException(("Could not find any available servers"));
        }

        for (ServerDescription description : myServer.getData().getOnlineServers()) {
            if (description.getType() == type) {
                for (int i = 0; i< description.getNcores(); ++i) {
                    onlineTypedNodes.add(description);
                }
                weightedSum += description.getNcores();
            }
        }

        Random random = new Random();
        int weightedRandom = random.nextInt(weightedSum);

        return onlineTypedNodes.get(weightedRandom);
    }

    @Override
    public ServerDescription getNextServer() throws ServiceUnavailableException {
        List<ServerDescription> onlineTypedNodes = new ArrayList<ServerDescription>();
        int weightedSum = 0;

        if (myServer.getData().getOnlineServersSize() == 0) {
            throw new ServiceUnavailableException(("Could not find any available servers"));
        }

        for (ServerDescription description : myServer.getData().getOnlineServers()) {
            for (int i = 0; i< description.getNcores(); ++i) {
                onlineTypedNodes.add(description);
            }
            weightedSum += description.getNcores();
        }

        Random random = new Random();
        int weightedRandom = random.nextInt(weightedSum);

        return onlineTypedNodes.get(weightedRandom);
    }

}
