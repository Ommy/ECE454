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

    private interface IParser {
        void parse(final GraphParameters params, final BufferedReader br) throws IOException;
    }

    private GraphParameters readInput(IParser parser) {
        final InputStream istream = new ByteArrayInputStream(input);
        final BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        GraphParameters params = null;

        try {
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
            parser.parse(params, br);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        final ArrayList<ArrayList<Integer>> allEdges = new ArrayList<ArrayList<Integer>>();

        final GraphParameters params = readInput(new IParser() {
            @Override
            public void parse(final GraphParameters params, final BufferedReader br) throws IOException {

                String parts[] = null;
                String strLine = null;

                while ((strLine = br.readLine()) != null && !strLine.equals("")) {
                    parts = strLine.split(": ");

                    final Integer vertex = Integer.parseInt(parts[0]);
                    final ArrayList<Integer> edges = new ArrayList<Integer>();

                    if (parts.length > 1) {
                        parts = parts[1].split(" +");
                        for (String part : parts) {
                            final Integer edge = Integer.parseInt(part);
                            edges.add(edge);
                        }
                    }

                    allEdges.add(vertex, edges);
                }
            }
        });

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        // naive triangle counting algorithm
        for (int i = 0; i < params.numVertices; i++) {
            ArrayList<Integer> n1 = allEdges.get(i);
            for (int j: n1) {
                ArrayList<Integer> n2 = allEdges.get(j);
                for (int k: n2) {
                    ArrayList<Integer> n3 = allEdges.get(k);
                    for (int l: n3) {
                        if (i < j && j < k && l == i) {
                            triangles.add(new Triangle(i, j, k));
                        }
                    }
                }
            }
        }

        return triangles;
    }

    private List<Triangle> enumerateTrianglesMultiThreaded() throws IOException {

        long beginTime = System.currentTimeMillis();

        final ArrayList<ArrayList<Integer>> smallerEdges = new ArrayList<ArrayList<Integer>>();
        final ArrayList<ArrayList<Integer>> biggerEdges = new ArrayList<ArrayList<Integer>>();
        final ArrayList<Set<Integer>> allEdges = new ArrayList<Set<Integer>>();

        final GraphParameters params = readInput(new IParser() {
            @Override
            public void parse(final GraphParameters params, final BufferedReader br) throws IOException {

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
                    smallerEdges.add(vertex, tempSmallEdges);
                    biggerEdges.add(vertex, tempBigEdges);
                    allEdges.add(vertex, tempEdges);
                }
            }
        });

        long parseTime = System.currentTimeMillis();
        System.out.println("Parse time     : " + (parseTime - beginTime));

        long timeA = 0;
        long timeB = 0;
        long timeC = 0;
        long timeD = 0;

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        for (int vertex = 0; vertex < params.numVertices; vertex++) {
            final List<Integer> smallEdges = smallerEdges.get(vertex);

            long x = System.currentTimeMillis();

            for (int smallVertex : smallEdges) {
                final List<Integer> bigEdges = biggerEdges.get(vertex);

                long y = 0;
                if (debug) {
                    y = System.currentTimeMillis();
                }

                for (int bigVertex : bigEdges) {

                    long z = 0;
                    if (debug) {
                        z = System.currentTimeMillis();
                    }

                    if (allEdges.get(smallVertex).contains(bigVertex)) {

                        long w = 0;
                        if (debug) {
                            w = System.currentTimeMillis();
                        }

                        triangles.add(new Triangle(smallVertex, vertex, bigVertex));

                        if (debug) {
                            timeD += System.currentTimeMillis() - w;
                        }
                    }

                    if (debug) {
                        timeC += System.currentTimeMillis() - z;
                    }
                }

                if (debug) {
                    timeB += System.currentTimeMillis() - y;
                }
            }

            if (debug) {
                timeA += System.currentTimeMillis() - x;
            }
        }

        long finishTime = System.currentTimeMillis();

        System.out.println("Triangle time  : " + (finishTime - parseTime) / 1000);
        System.out.println("Total time     : " + (finishTime - beginTime) / 1000);

        if (debug) {
            System.out.println("Triangle time A : " + timeA);
            System.out.println("Triangle time B : " + timeB);
            System.out.println("Triangle time C : " + timeC);
            System.out.println("Triangle time D : " + timeD);
        }

        return triangles;
    }

}
