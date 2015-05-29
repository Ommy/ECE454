package servers;

import ece454750s15a1.ServerData;
import ece454750s15a1.ServerDescription;

public interface IServer {

    boolean isSeedNode(String host, int mport);

    ServerDescription getDescription();

    ServerData getData();

    void updateData(ServerData data);
}
