/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TriangleCountImpl {

    private static final boolean debug = false;
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

    public List<Triangle> enumerateTriangles() throws IOException {

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

        final InputStream istream = new ByteArrayInputStream(input);
        final BufferedReader br = new BufferedReader(new InputStreamReader(istream));

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

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

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

    private class EnumerateTriangleRunnable implements Runnable {
        @Override
        public void run() {

        }
    }


    private List<Triangle> enumerateTrianglesMultiThreaded() throws IOException {
        long beginTime = System.currentTimeMillis();

        final InputStream istream = new ByteArrayInputStream(input);
        final BufferedReader br = new BufferedReader(new InputStreamReader(istream));

        final GraphParameters params = checkFirstLine(br);

        final List<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>();
        final List<ArrayList<Integer>> biggerEdges = new ArrayList<ArrayList<Integer>>();
        final List<Set<Integer>> allEdges = new ArrayList<Set<Integer>>();

        String parts[] = null;
        String strLine = null;

        while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
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

        final ExecutorService executorService = Executors.newFixedThreadPool(numCores);

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

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

}
