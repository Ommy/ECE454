package servers;

import ece454750s15a1.ServerData;
import ece454750s15a1.ServerDescription;

public interface IServer {

    public boolean isSeedNode(String host, int mport);

    public ServerDescription getDescription();

    public ServerData getData();

    public void updateData(ServerData data);
}
