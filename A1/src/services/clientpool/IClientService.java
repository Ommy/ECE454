package services.clientpool;

import ece454750s15a1.ServerDescription;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;

public interface IClientService<U, UA> {

    <T> T callOnce(String host, int port, U request);

    <T> T call(ServerDescription targetServer, U request);
}
