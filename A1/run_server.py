import os

run_fe_server = "./run_fe_server_windows.sh"
run_be_server = "./run_be_server_windows.sh"
run_management_client = "./run_managementclient_windows.sh"
run_password_client = "./run_passwordclient_windows.sh"

args = " -host %s -pport %s -mport %s -ncores %s -seeds %s -type %s"
host = ""
pport = ""
mport = ""
ncores = ""
seeds = ""
stype = ""

be_servers = []
fe_servers = []

def handle_option(c):
    run = ""
    if c == "FE":
        print "Enter 6 arguments, space delimited"
        print args
        c = raw_input("Enter arguments >> ")
        run = run_fe_server
    elif c == "BE":
        print "Enter 6 arguments, space delimited"
        print args
        c = raw_input("Enter arguments >> ")
        run = run_be_server
    elif c == "PC":
        print "Enter 6 arguments, space delimited"
        print args
        c = raw_input("Enter arguments >> ")
        run = run_password_client
    elif c == "MC":
        print "Enter 6 arguments, space delimited"
        print args
        c = raw_input("Enter arguments >> ")
        run = run_management_client
    elif c == "BB":
        print be_servers
        return
    elif c == "FB":
        print fe_servers
        return
    else:
        return

    c = c.split(" ")
    host = c[0]
    pport = c[1]
    mport = c[2]
    ncores = c[3]
    seeds = c[4]
    stype = c[5]
    s = args % (host, pport, mport, ncores, seeds, stype)
    s = run + s

    print "Argument"
    print s

    os.system(s)

    if c == "FE":
        fe_servers.append(s)
    elif c == "BE":
        be_servers.append(s)

    return s

def print_options():
    print "FE - Launch FE Server"
    print "BE - Launch BE Server"
    print "PC - Password Client"
    print "MC - Management Client"
    print "BB - BE Servers"
    print "FB - FE Servers"
    print "Q - quit"

def main():
    c = "g"
    while c and c != "Q":
        print_options()
        c = raw_input("Enter >> ")
        handle_option(c)

if __name__ == "__main__":
    main()
