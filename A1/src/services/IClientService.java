package services;

import ece454750s15a1.ServerDescription;

public interface IClientService {

    <T> T callOnce(String host, int mport, IManagementServiceRequest request);

    <T> T call(ServerDescription targetServer, IManagementServiceRequest request);

    <T> T call(ServerDescription targetServer, IPasswordServiceRequest request);
}
