namespace java ece454750s15a1

struct PerfCounters {
    1:i32 numSecondUp,
    2:i32 numRequestsReceived,
    3:i32 numRequestsCompleted
}

exception ServiceUnavailableException {
    1: string msg
}

service A1Password {
    #Hash Password
    string hashPassword(1: string password, 2:i16 logRounds) throws (1: ServiceUnavailableException e)
    bool checkPassword(1: string password, 2: string hash)
    PerfCounters getPerfCounters()
}
