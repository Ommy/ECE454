package servers;

public abstract class Server {
    String host;
    int pport;
    int mport;
    int ncores;
    Map<String, Integer> seeds;

    public Server() {
        seeds = new HashMap<String, Integer>();
    }

    public void initialize(String[] args) {
        List<String> seedsList = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-host") && i+1 < args.length) {
                host = args[i+1];
            } else if (args[i].equals("-pport") && i+1 < args.length) {
                pport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-mport") && i+1 < args.length) {
                mport = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-ncores") && i+1 < args.length) {
                ncores = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-seeds") && i+1 < args.length) {
                seedsList = Arrays.asList(args[i + 1].split(","));
            }
        }

        for(String seed: seedsList) {
            String[] splitSeed = seed.split(":");
            seeds.put(splitSeed[0], Integer.parseInt(splitSeed[1]));
        }
    }
}
