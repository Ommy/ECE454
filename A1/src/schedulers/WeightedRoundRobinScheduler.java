package schedulers;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;
import ece454750s15a1.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servers.IServer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WeightedRoundRobinScheduler implements IScheduler {
    private final IServer myServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomWeightedCoresScheduler.class.toString());
    private static ServerDescription lastServerReturned;

    public WeightedRoundRobinScheduler(IServer server) {
        myServer = server;
    }

    @Override
    public ServerDescription getNextServerByType(ServerType type) throws ServiceUnavailableException {
        int idx;
        int currentWeight = 0;
        Random random = new Random();
        List<ServerDescription> servers = new ArrayList<ServerDescription>();
        for (ServerDescription server: myServer.getData().getOnlineServers()) {
            if (server.getType() == type) {
                servers.add(server);
            }
        }

        if (servers.size() == 0) {
            throw new ServiceUnavailableException("No online servers!");
        }

        List<Integer> weights = new ArrayList<Integer>();
        for (ServerDescription server: servers) {
            weights.add(server.getNcores());
        }

        if (lastServerReturned == null) {
            idx = -1;
        } else {
            idx = myServer.getData().getOnlineServers().indexOf(lastServerReturned);
        }

        while (true) {
            idx = (idx + 1) % servers.size();
            if (idx == 0) {
                currentWeight = currentWeight - gcd(weights);
                if (currentWeight <= 0) {
                    currentWeight = max(weights);
                    if (currentWeight == 0) {
                        lastServerReturned = servers.get(random.nextInt(servers.size()));
                        break;
                    }
                }
            }
            if (weights.get(idx) >= currentWeight) {
                lastServerReturned = servers.get(idx);
                break;
            }
        }

        LOGGER.info("Chose server: " + lastServerReturned);
        return lastServerReturned;
    }

    @Override
    public ServerDescription getNextServer() throws ServiceUnavailableException {
        int idx;
        int currentWeight = 0;
        Random random = new Random();
        List<ServerDescription> servers = myServer.getData().getOnlineServers();
        List<Integer> weights = new ArrayList<Integer>();
        for (ServerDescription server: servers) {
            weights.add(server.getNcores());
        }

        if (lastServerReturned == null) {
            idx = -1;
        } else {
            idx = myServer.getData().getOnlineServers().indexOf(lastServerReturned);
        }

        while (true) {
            idx = (idx + 1) % myServer.getData().getOnlineServersSize();
            if (idx == 0) {
                currentWeight = currentWeight - gcd(weights);
                if (currentWeight <= 0) {
                    currentWeight = max(weights);
                    if (currentWeight == 0) {
                        return servers.get(random.nextInt(servers.size()));
                    }
                }
            }
            if (weights.get(idx) >= currentWeight) {
                return servers.get(idx);
            }
        }
    }

    private int max(List<Integer> elements) {
        return Collections.max(elements);
    }

    private int gcd(List<Integer> elements) {
        int result = elements.get(0);
        for (int i = 0; i < elements.size(); i++) {
            result = BigInteger.valueOf((long)result).gcd(BigInteger.valueOf((long)elements.get(i))).intValue();
        }
        return result;
    }

}
