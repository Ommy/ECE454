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

        GraphParameters params = null;

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

        params = new GraphParameters(numVertices, numEdges);

        return params;
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

        GraphParameters params = checkFirstLine(br);

        final List<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>();
        final List<ArrayList<Integer>> biggerEdges = new ArrayList<ArrayList<Integer>>();
        final List<Set<Integer>> allEdges = new ArrayList<Set<Integer>>();

        String parts[] = null;
        String strLine = null;
        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            parts = strLine.split(": ");

            final Integer vertex = Integer.parseInt(parts[0]);

            final Set<Integer> tempEdges = new HashSet<Integer>();
            final ArrayList<Integer> tempSmallEdges = new ArrayList<Integer>();
            final ArrayList<Integer> tempBigEdges = new ArrayList<Integer>();

            if (parts.length > 1) {
                parts = parts[1].split(" ");
                for (String part: parts) {
                    final Integer edge = Integer.parseInt(part);
                    if (edge < vertex) {
                        tempSmallEdges.add(edge);
                    } else {
                        tempBigEdges.add(edge);
                    }
                    tempEdges.add(edge);
                }
            }

            smallerEdges.add(tempSmallEdges);
            biggerEdges.add(tempBigEdges);
            allEdges.add(tempEdges);
        }

        final ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        for (int vertex = 0; vertex < params.numVertices; vertex++) {
            for (Integer smallVertex : smallerEdges.get(vertex)) {
                for (Integer bigVertex : biggerEdges.get(vertex)) {
                    if (allEdges.get(smallVertex).contains(bigVertex)) {
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

        final List<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>();
        final List<ArrayList<Integer>> biggerEdges = new ArrayList<ArrayList<Integer>>();
        final List<Set<Integer>> allEdges = new ArrayList<Set<Integer>>();

        String strLine = null;
        while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
            String parts[] = strLine.split(": ");

            final Integer vertex = Integer.parseInt(parts[0]);

            final Set<Integer> tempEdges = new HashSet<Integer>();
            final ArrayList<Integer> tempSmallEdges = new ArrayList<Integer>();
            final ArrayList<Integer> tempBigEdges = new ArrayList<Integer>();

            if (parts.length > 1) {
                parts = parts[1].split(" ");
                for (String part: parts) {
                    final Integer edge = Integer.parseInt(part);
                    if (edge < vertex) {
                        tempSmallEdges.add(edge);
                    } else {
                        tempBigEdges.add(edge);
                    }
                    tempEdges.add(edge);
                }
            }

            smallerEdges.add(tempSmallEdges);
            biggerEdges.add(tempBigEdges);
            allEdges.add(tempEdges);
        }

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        final List<Callable<List<Triangle>>> callables = new ArrayList<Callable<List<Triangle>>>();
        for (int i = 0; i < numCores; i++) {
            callables.add(new EnumerateTriangleCallable(i, smallerEdges, biggerEdges, allEdges));
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

        private final Integer position;
        private final List<ArrayList<Integer>> smallerEdges;
        private final List<ArrayList<Integer>> biggerEdges;

        private final List<Set<Integer>> allEdges;
        private final List<Triangle> triangles = new ArrayList<Triangle>();

        public EnumerateTriangleCallable(
                final int position,
                final List<ArrayList<Integer>> smallerEdges,
                final List<ArrayList<Integer>> biggerEdges,
                final List<Set<Integer>> allEdges) {
            this.position = position;
            this.smallerEdges = smallerEdges;
            this.biggerEdges = biggerEdges;
            this.allEdges = allEdges;
        }

        @Override
        public List<Triangle> call() {
            for (int i = position; i < allEdges.size(); i+=numCores) {
                for (Integer smallVertex : smallerEdges.get(i)) {
                    for (Integer bigVertex : biggerEdges.get(i)) {
                        if (allEdges.get(smallVertex).contains(bigVertex)) {
                            triangles.add(new Triangle(smallVertex, i, bigVertex));
                        }
                    }
                }
            }
            return triangles;
        }
    }

}
