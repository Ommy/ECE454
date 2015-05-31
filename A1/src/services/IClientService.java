package services;

import ece454750s15a1.ServerDescription;
import requests.IManagementServiceAsyncRequest;
import requests.IManagementServiceRequest;
import requests.IPasswordServiceAsyncRequest;
import requests.IPasswordServiceRequest;

public interface IClientService {

    <T> T callOnce(String host, int mport, IManagementServiceRequest request);

    <T> T call(ServerDescription targetServer, IManagementServiceRequest request);

    <T> T call(ServerDescription targetServer, IPasswordServiceRequest request);

    <T> T callOnceAsync(String host, int mport, IManagementServiceAsyncRequest request);

    <T> T callAsync(ServerDescription targetServer, IManagementServiceAsyncRequest request);

    <T> T callAsync(ServerDescription targetServer, IPasswordServiceAsyncRequest request);
}
