package services.clientpool;

import ece454750s15a1.ServerDescription;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;

public interface IClientService<U, UA> {

    <T> T callOnce(String host, int mport, U request);

    <T> T call(ServerDescription targetServer, U request);

    <T> T callOnceAsync(String host, int mport, UA request);

    <T> T callAsync(ServerDescription targetServer, UA request);
}
