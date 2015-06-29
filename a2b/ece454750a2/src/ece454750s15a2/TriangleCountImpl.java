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

    private class GraphParameters {
        public Integer numVertices;
        public Integer numEdges;

        public GraphParameters(int numVertices, int numEdges) {
            this.numVertices = numVertices;
            this.numEdges = numEdges;
        }
    }

    private GraphParameters checkFirstLine(final BufferedReader br) throws IOException {
        String strLine = br.readLine();
        String parts[] = strLine.split(", ");
        String verticesParts[] = parts[0].split(" ");
        String edgesParts[] = parts[1].split(" ");
        int numVertices = Integer.parseInt(verticesParts[0]);
        int numEdges = Integer.parseInt(edgesParts[0]);

        if (!verticesParts[1].contains("vertices") || !edgesParts[1].contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }

        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

        return new GraphParameters(numVertices, numEdges);
    }

    public List<Triangle> enumerateTriangles() throws IOException, InterruptedException, ExecutionException {

        List<Triangle> triangles = null;
        if (numCores == 1) {
            triangles = enumerateTrianglesSingleThreaded();
        } else {
            triangles = enumerateTrianglesMultiThreaded();
        }

        System.out.println("Number of triangles found: " + triangles.size());

        return triangles;
    }

    private List<Triangle> enumerateTrianglesSingleThreaded() throws IOException {
        long beginTime = System.currentTimeMillis();

        final BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input)));

        final GraphParameters params = checkFirstLine(br);

        final List<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>(params.numVertices);
        final List<HashSet<Integer>> biggerEdges = new ArrayList<HashSet<Integer>>(params.numVertices);

        String strLine = null;
        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            String parts[] = strLine.split(": ");

            final Integer vertex = Integer.parseInt(parts[0]);

            final ArrayList<Integer> smallEdges = new ArrayList<Integer>();
            final ArrayList<Integer> bigEdges = new ArrayList<Integer>();

            if (parts.length > 1) {
                parts = parts[1].split(" ");

                for (String part: parts) {
                    final Integer edge = Integer.parseInt(part);
                    if (edge < vertex) {
                        smallEdges.add(edge);
                    } else {
                        bigEdges.add(edge);
                    }
                }
            }

            Collections.sort(smallEdges);

            smallerEdges.add(smallEdges);
            biggerEdges.add(new HashSet<Integer>(bigEdges));
        }

        final ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        for (int vertex = 0; vertex < params.numVertices; vertex++) {
            for (Integer smallVertex : smallerEdges.get(vertex)) {
                for (Integer bigVertex : biggerEdges.get(vertex)) {
                    if (biggerEdges.get(smallVertex).contains(bigVertex)) {
                        triangles.add(new Triangle(smallVertex, vertex, bigVertex));
                    }
                }
            }
        }

        long finishTime = System.currentTimeMillis();

        System.out.println("Triangle time  : " + (finishTime - parseTime));
        System.out.println("Total time     : " + (finishTime - beginTime));

        return triangles;
    }

    private List<Triangle> enumerateTrianglesMultiThreaded() throws IOException, InterruptedException, ExecutionException {
        long beginTime = System.currentTimeMillis();

        final BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input)));

        final GraphParameters params = checkFirstLine(br);

        final List<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>(params.numVertices);
        final List<HashSet<Integer>> biggerEdges = new ArrayList<HashSet<Integer>>(params.numVertices);

        String strLine = null;
        while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
            String parts[] = strLine.split(": ");

            final Integer vertex = Integer.parseInt(parts[0]);

            final ArrayList<Integer> smallEdges = new ArrayList<Integer>();
            final ArrayList<Integer> bigEdges = new ArrayList<Integer>();

            if (parts.length > 1) {
                parts = parts[1].split(" ");
                for (String part: parts) {
                    final Integer edge = Integer.parseInt(part);
                    if (edge < vertex) {
                        smallEdges.add(edge);
                    } else {
                        bigEdges.add(edge);
                    }
                }
            }

            Collections.sort(smallEdges);

            smallerEdges.add(smallEdges);
            biggerEdges.add(new HashSet<Integer>(bigEdges));
        }

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        final List<Callable<List<Triangle>>> callables = new ArrayList<Callable<List<Triangle>>>();
        for (int i = 0; i < numCores; i++) {
            callables.add(new EnumerateTriangleCallable(i, numCores, smallerEdges, biggerEdges));
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(numCores);
        final List<Future<List<Triangle>>> futures = new ArrayList<Future<List<Triangle>>>();
        for (int i = 0; i < numCores; i++) {
            futures.add(executorService.submit(callables.get(i)));
        }

        final List<Triangle> triangles = new ArrayList<Triangle>();
        for (Future<List<Triangle>> future : futures) {
            triangles.addAll(future.get());
        }

        long finishTime = System.currentTimeMillis();

        System.out.println("Triangle time  : " + (finishTime - parseTime));
        System.out.println("Total time     : " + (finishTime - beginTime));

        return triangles;
    }

    private class EnumerateTriangleCallable implements Callable<List<Triangle>> {

        private final int initialPosition;
        private final int step;
        private final List<ArrayList<Integer>> smallerEdges;
        private final List<HashSet<Integer>> biggerEdges;

        public EnumerateTriangleCallable(
                final int initialPosition,
                final int step,
                final List<ArrayList<Integer>> smallerEdges,
                final List<HashSet<Integer>> biggerEdges) {
            this.initialPosition = initialPosition;
            this.step = step;
            this.smallerEdges = smallerEdges;
            this.biggerEdges = biggerEdges;
        }

        @Override
        public List<Triangle> call() {
            final List<Triangle> triangles = new ArrayList<Triangle>();

            for (int i = initialPosition; i < smallerEdges.size(); i += step) {
                for (Integer smallVertex : smallerEdges.get(i)) {
                    for (Integer bigVertex : biggerEdges.get(i)) {
                        if (biggerEdges.get(smallVertex).contains(bigVertex)) {
                            triangles.add(new Triangle(smallVertex, i, bigVertex));
                        }
                    }
                }
            }
            return triangles;
        }
    }

}
