package utilities;

import ece454750s15a1.ServerDescription;
import ece454750s15a1.ServerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerDescriptionParser {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PPORT = 6719;
    private static final int DEFAULT_MPORT = 4848;
    private static final int DEFAULT_NCORES = 2;
    private static final ServerType DEFAULT_TYPE = ServerType.FE;

    private static final String PARAMETER_HOST = "-host";
    private static final String PARAMETER_PPORT = "-pport";
    private static final String PARAMETER_MPORT = "-mport";
    private static final String PARAMETER_NCORES = "-ncores";
    private static final String PARAMETER_SEEDS = "-seeds";
    private static final String PARAMETER_TYPE = "-type";

    public ServerDescription parse(String[] args, ServerType type) {
        String host = DEFAULT_HOST;
        int pport = DEFAULT_PPORT;
        int mport = DEFAULT_MPORT;
        int ncores = DEFAULT_NCORES;
        List<String> seedsList = new ArrayList<String>();
        ServerType otype = type;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(PARAMETER_HOST) && (i+1 < args.length)) {
                host = args[i+1];
            } else if (args[i].equals(PARAMETER_PPORT) && (i+1 < args.length)) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals(PARAMETER_MPORT) && (i+1 < args.length)) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals(PARAMETER_NCORES) && (i+1 < args.length)) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals(PARAMETER_SEEDS) && (i+1 < args.length)) {
                seedsList = Arrays.asList(args[i+1].split(","));
            } else if (args[i].equals(PARAMETER_TYPE) && (i+1 < args.length)) {
                otype = ServerType.valueOf(args[i+1]);
            }
        }

        ServerDescription description = new ServerDescription(host, pport, mport, ncores, otype, seedsList);
        return description;
    }
}
