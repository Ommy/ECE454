package services.clientpool;

import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.ServerDescription;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class BaseClientPoolService<T extends TServiceClient, TA extends TAsyncClient> implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseClientPoolService.class.getName());

    protected String hash(ServerDescription server) {
        return server.getHost() + "," + server.getPport() + "," + server.getMport();
    }

    protected <T extends TServiceClient> void handleClientFailed(ServerDescription server, final HashMap<String, ConcurrentLinkedQueue<Client<T>>> map) {
        LOGGER.info("Removing client from client pool");

        if (map.containsKey(hash(server))) {
            ConcurrentLinkedQueue<Client<T>> queue = map.get(hash(server));
            for (Client<T> client: queue) {
                client.close();
            }
            map.remove(hash(server));
        }
    }
}
