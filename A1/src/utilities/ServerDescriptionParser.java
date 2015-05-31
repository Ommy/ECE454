package utilities;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDescriptionParser {
    public ServerDescription parse(String[] args, ServerType type) {
        String host = "localhost";
        int pport = 6719;
        int mport = 4848;
        int ncores = 1;

        List<String> seedsList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && (i+1 < args.length)) {
                host = args[i+1];
            } else if (args[i].equals("-pport") && (i+1 < args.length)) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-mport") && (i+1 < args.length)) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-ncores") && (i+1 < args.length)) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-seeds") && (i+1 < args.length)) {
                seedsList = Arrays.asList(args[i + 1].split(","));
            }
        }

        ServerDescription description = new ServerDescription(host, pport, mport, ncores, type, seedsList);
        return description;
    }
}
