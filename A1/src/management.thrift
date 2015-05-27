namespace java ece454750s15a1

// required interface
struct PerfCounters {
    1:i32 numSecondUp,
    2:i32 numRequestsReceived,
    3:i32 numRequestsCompleted
}

// custom interfaces
enum ServerStatus {
    UNREGISTERED,
    REGISTERED,
    DOWN
}

enum ServerType {
    SEED,
    FE,
    BE
}

struct ServerDescription {
    1:string host,
    2:i32 pport,
    3:i32 mport,
    4:i32 ncores,
    5:ServerStatus status,
    6:ServerType type   
}

struct ServerData {
    1:list<ServerDescription> knownServers
}

service A1Management {
    # required services
    list<string> getGroupMembers()
    PerfCounters getPerfCounters()

    # custom services
    ServerData exchangeServerData(1:ServerData data);
}
