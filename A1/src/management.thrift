namespace java ece454750s15a1

enum ServerStatus {
    UNREGISTERED,
    REGISTERED,
    DOWN
}

struct ServerDescription {
    1:i32 id,
    2:string host,
    3:i32 pport,
    4:i32 mport,
    5:i32 ncores,
    6:ServerStatus status
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
    bool registerNode(1:ServerDescription description)
    bool announceNode(1:ServerDescription description)
}
