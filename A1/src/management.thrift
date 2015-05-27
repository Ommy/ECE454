namespace java ece454750s15a1

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
    1:i32 id,
    2:string host,
    3:i32 pport,
    4:i32 mport,
    5:i32 ncores,
    6:ServerStatus status,
    7:ServerType type
}

struct PerfCounters {
    1:i32 numSecondUp,
    2:i32 numRequestsReceived,
    3:i32 numRequestsCompleted
}

service A1Management {
    # required services
    list<string> getGroupMembers()
    PerfCounters getPerfCounters()

    # custom services
    list<ServerDescription> onStartupRegisterNode(1:ServerDescription newNodeDescription)
    list<ServerDescription> onStartupRequestNodes(1:ServerDescription newNodeDescription)


    bool announceNode(1:ServerDescription description)
}
