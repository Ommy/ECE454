namespace java ece454750s15a1

// required interface
struct PerfCounters {
    1:i32 numSecondUp,
    2:i32 numRequestsReceived,
    3:i32 numRequestsCompleted
}

// custom interfaces
enum ServerType {
    FE,
    BE
}

struct ServerDescription {
    1:string host,
    2:i32 pport,
    3:i32 mport,
    4:i32 ncores,
    5:ServerType type
}

struct ServerData {
    1:list<ServerDescription> onlineServers,
    2:list<ServerDescription> offlineServers
}

service A1Management {
    # required services
    list<string> getGroupMembers()
    PerfCounters getPerfCounters()

    # custom services
    ServerData exchangeServerData(1:ServerData data);
}
