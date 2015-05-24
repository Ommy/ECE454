namespace java ece454750s15a1

enum ServerStatus {
    UNREGISTERED,
    REGISTERED,
    DOWN
}

struct ServerDescription {
    1:string host,
    2:i32 pport,
    3:i32 mport,
    4:i32 ncores,
    5:ServerStatus status
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
    bool register(1:ServerDescription)
    bool announce(1:ServerDescription)
}
