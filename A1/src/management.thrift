namespace java ece454750s15a1

struct PerfCounters {
    1:i32 numSecondUp,
    2:i32 numRequestsReceived,
    3:i32 numRequestsCompleted
}

service A1Management {
    list<string> getGroupMembers()
    PerfCounters getPerfCounters()
}
