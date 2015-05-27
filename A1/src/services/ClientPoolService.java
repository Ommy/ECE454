package services;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import org.apache.thrift.TServiceClient;

import java.util.List;

public class ClientPoolService {
    List<A1Password.Client> passwordClients;
    List<A1Management.Client> managementClients;

    private void startupClient() {}

}
