package servers;

import ece454750s15a1.PerfCounters;
import ece454750s15a1.ServerData;
import ece454750s15a1.ServerDescription;
import services.ServiceExecutor;

public interface IServer {

    ServerDescription getDescription();

    ServerData getData();

    ServiceExecutor getServiceExecutor();

    PerfCounters getPerfCounters();

    void updateData(ServerData data);

    void onConnectionFailed(ServerDescription failedServer);
}
