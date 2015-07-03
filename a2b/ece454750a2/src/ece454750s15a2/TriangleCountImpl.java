/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TriangleCountImpl {

    private byte[] input;
    private int numCores;

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.numCores = numCores;
    }

    public List<String> getGroupMembers() {
        return new ArrayList<String>(Arrays.asList("aemorais", "faawan", "v6lai"));
    }

    private class Graph {
        public final BufferedReader bufferedReader;

        public int numVertices;
        public int numEdges;

        public Graph(final byte[] input) {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input)));
            this.numVertices = 0;
            this.numEdges = 0;
        }

        void readParameters() throws IOException {
            final String strLine = bufferedReader.readLine();
            final String parts[] = strLine.split(", ", 2);
            final String verticesParts[] = parts[0].split(" ", 2);
            final String edgesParts[] = parts[1].split(" ", 2);
            final int numVertices = Integer.parseInt(verticesParts[0]);
            final int numEdges = Integer.parseInt(edgesParts[0]);

            if (!verticesParts[1].contains("vertices") || !edgesParts[1].contains("edges")) {
                System.err.println("Invalid graph file format. Offending line: " + strLine);
                System.exit(-1);
            }

            System.err.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

            this.numVertices = numVertices;
            this.numEdges = numEdges;
        }

        void close() throws IOException {
            bufferedReader.close();
        }
    }

    public List<Triangle> enumerateTriangles() throws IOException, InterruptedException, ExecutionException {

        final Graph graph = new Graph(input);

        List<Triangle> triangles;

        try {
            graph.readParameters();

            if (numCores == 1) {
                triangles = enumerateTrianglesSingleThreaded(graph);
            } else {
                triangles = enumerateTrianglesMultiThreaded(graph);
            }
        } finally {
            graph.close();
        }

        if (triangles == null) {
            triangles = new ArrayList<Triangle>();
        }

        System.out.println("Number of triangles found: " + triangles.size());
        return triangles;
    }

    private List<Triangle> enumerateTrianglesSingleThreaded(final Graph graph) throws IOException {

        // initialize biggerNeighbours adjacency list
        final ArrayList<Integer>[] biggerNeighbours = new ArrayList[graph.numVertices];
        for (int i = 0; i < graph.numVertices; i++) {
            biggerNeighbours[i] = new ArrayList<Integer>();
        }

        // parse graph into biggerNeighbours adjacency list
        String line = null;
        while ((line = graph.bufferedReader.readLine()) != null && !line.equals("")) {
            parseBiggerNeighbourLine(biggerNeighbours, line);
        }

        // count triangles
        final List<Triangle> triangles = enumerateTriangles(biggerNeighbours);

        return triangles;
    }

    private List<Triangle> enumerateTrianglesMultiThreaded(final Graph graph) throws IOException, InterruptedException, ExecutionException {

        // initialize biggerNeighbours adjacency list
        final List<Integer>[] biggerNeighbours = new ArrayList[graph.numVertices];
        for (int i = 0; i < graph.numVertices; i++) {
            biggerNeighbours[i] = new ArrayList<Integer>();
        }

        // parse graph into biggerNeighbours adjacency list
        final ExecutorService runnableExecutorService = Executors.newFixedThreadPool(numCores);

        String line = null;
        while ((line = graph.bufferedReader.readLine()) != null && !line.equals("")) {
            runnableExecutorService.execute(new ParseInputEdgesRunnable(line, biggerNeighbours));
        }

        runnableExecutorService.shutdown();
        runnableExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // count triangles
        final List<Callable<List<Triangle>>> callables = new ArrayList<Callable<List<Triangle>>>();
        for (int i = 0; i < numCores; i++) {
            callables.add(new EnumerateTriangleCallable(i, numCores, biggerNeighbours));
        }

        final ExecutorService callableExecutorService = Executors.newFixedThreadPool(numCores);
        final List<Future<List<Triangle>>> futures = new ArrayList<Future<List<Triangle>>>();
        for (int i = 0; i < numCores; i++) {
            futures.add(callableExecutorService.submit(callables.get(i)));
        }

        final List<Triangle> triangles = new ArrayList<Triangle>();
        for (Future<List<Triangle>> future : futures) {
            triangles.addAll(future.get());
        }

        return triangles;
    }

    private static void parseBiggerNeighbourLine(final List<Integer>[] biggerNeighbours, String line) {
        final String parts[] = line.split(": ", 2);

        final Integer vertex = Integer.parseInt(parts[0]);

        final ArrayList<Integer> bigNeighbours = new ArrayList<Integer>();
        if (parts.length > 1) {
            final StringTokenizer tokenizer = new StringTokenizer(parts[1]);
            while (tokenizer.hasMoreTokens()) {
                final Integer edge = Integer.parseInt(tokenizer.nextToken());
                if (edge > vertex) {
                    bigNeighbours.add(edge);
                }
            }
        }

        Collections.sort(bigNeighbours);
        biggerNeighbours[vertex] = new ArrayList<Integer>(bigNeighbours);
    }

    private static List<Triangle> enumerateTriangles(final List<Integer>[] biggerNeighbours) {
        return enumerateTriangles(biggerNeighbours, 0, 1);
    }

    private static List<Triangle> enumerateTriangles(final List<Integer>[] biggerNeighbours, int initialPosition, int step) {
        final List<Triangle> triangles = new ArrayList<Triangle>();

        for (int smallVertex = initialPosition; smallVertex < biggerNeighbours.length; smallVertex += step) {

            List<Integer> smallNeighbours = biggerNeighbours[smallVertex];

            for (int mediumVertex : smallNeighbours) {

                // from sorted ordering and pre-processing, all elements in smallNeighbours are larger than smallVertex

                List<Integer> mediumNeighbours = biggerNeighbours[mediumVertex];

                // small and medium are sorted min->max
                // iterate two pointers backwards from max->min searching for equal elements

                Integer i_s = smallNeighbours.size()-1;
                Integer i_m = mediumNeighbours.size()-1;

                while (i_s >= 0 && i_m >= 0) {

                    final int check_s = smallNeighbours.get(i_s);
                    final int check_m = mediumNeighbours.get(i_m);

                    if (check_m <= smallVertex || check_s <= mediumVertex) {
                        break;
                    }

                    if (check_s < check_m) {
                        --i_m;
                    } else if (check_s > check_m) {
                        --i_s;
                    } else {
                        triangles.add(new Triangle(smallVertex, mediumVertex, check_m));
                        --i_s;
                        --i_m;
                    }
                }
            }
        }

        return triangles;
    }

    private class ParseInputEdgesRunnable implements Runnable {
        final String line;
        final List<Integer>[] biggerNeighbours;

        public ParseInputEdgesRunnable(
                final String line,
                final List<Integer>[] biggerNeighbours) {
            this.line = line;
            this.biggerNeighbours = biggerNeighbours;
        }

        @Override
        public void run() {
            parseBiggerNeighbourLine(biggerNeighbours, line);
        }
    }

    private class EnumerateTriangleCallable implements Callable<List<Triangle>> {
        private final int initialPosition;
        private final int step;
        private final List<Integer>[] biggerNeighbours;

        public EnumerateTriangleCallable(
                final int initialPosition,
                final int step,
                final List<Integer>[] biggerNeighbours) {
            this.initialPosition = initialPosition;
            this.step = step;
            this.biggerNeighbours = biggerNeighbours;
        }

        @Override
        public List<Triangle> call() {
            return enumerateTriangles(biggerNeighbours, initialPosition, step);
        }
    }
}
