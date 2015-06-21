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
    private byte[] input;
    private int numCores;

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.numCores = numCores;
    }

    public List<String> getGroupMembers() {
        return new ArrayList<String>(Arrays.asList("aemorais", "faawan", "v6lai"));
    }

    public List<Triangle> enumerateTriangles() throws IOException {

        List<Triangle> triangles = null;
        if (numCores == 1) {
            triangles = enumerateTrianglesSingleThreaded();
        } else {
            triangles = enumerateTrianglesMultiThreaded();
        }

        return triangles;
    }

    private List<Triangle> enumerateTrianglesSingleThreaded() throws IOException {

        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        ArrayList<ArrayList<Integer>> adjacencyList = getAdjacencyList(input);
        int numVertices = adjacencyList.size();

        HashMap<Integer, ArrayList<Integer>> smallerEdges = new HashMap<Integer, ArrayList<Integer>>();
        HashMap<Integer, ArrayList<Integer>> biggerEdges = new HashMap<Integer, ArrayList<Integer>>();
        HashMap<Integer, Set<Integer>> allEdges = new HashMap<Integer, Set<Integer>>();

        for (int vertex = 0; vertex < numVertices; vertex++) {
            ArrayList<Integer> edges = adjacencyList.get(vertex);
            ArrayList<Integer> tempSmallEdges = new ArrayList<Integer>();
            ArrayList<Integer> tempBigEdges = new ArrayList<Integer>();

            int numEdges = edges.size();
            for (int edgeIndex = 0; edgeIndex < numEdges; edgeIndex++) {
                int edgeValue = edges.get(edgeIndex);
                if (edgeValue < vertex) {
                    tempSmallEdges.add(edgeValue);
                } else {
                    tempBigEdges.add(edgeValue);
                }
            }
            smallerEdges.put(vertex, tempSmallEdges);
            biggerEdges.put(vertex, tempBigEdges);
            final Set<Integer> tempEdges = new LinkedHashSet<Integer>(edges);
            allEdges.put(vertex, tempEdges);
        }

        for (int vertex = 0; vertex < numVertices; vertex++) {
            for (int smallVertex : smallerEdges.get(vertex)) {
                for (int bigVertex : biggerEdges.get(vertex)) {
                    if (allEdges.get(smallVertex).contains(bigVertex)) {
                        triangles.add(new Triangle(smallVertex, vertex, bigVertex));
                    }
                }
            }
        }

        System.out.println("Number of triangles found: " + triangles.size());

        return triangles;
    }

    private List<Triangle> enumerateTrianglesMultiThreaded() throws IOException {

        ArrayList<ArrayList<Integer>> adjacencyList = getAdjacencyList(input);
        ArrayList<Triangle> ret = new ArrayList<Triangle>();

        // naive triangle counting algorithm
        int numVertices = adjacencyList.size();
        for (int i = 0; i < numVertices; i++) {
            ArrayList<Integer> n1 = adjacencyList.get(i);
            for (int j: n1) {
                ArrayList<Integer> n2 = adjacencyList.get(j);
                for (int k: n2) {
                    ArrayList<Integer> n3 = adjacencyList.get(k);
                    for (int l: n3) {
                        if (i < j && j < k && l == i) {
                            ret.add(new Triangle(i, j, k));
                        }
                    }
                }
            }
        }

        System.out.println("Number of triangles found: " + ret.size());

        return ret;
    }

    public ArrayList<ArrayList<Integer>> getAdjacencyList(byte[] data) throws IOException {
        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();
        if (!strLine.contains("vertices") || !strLine.contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }

        String parts[] = strLine.split(", ");
        int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
        int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<Integer>());
        }
        while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
            parts = strLine.split(": ");
            int vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                for (String part: parts) {
                    adjacencyList.get(vertex).add(Integer.parseInt(part));
                }
            }
        }
        br.close();
        return adjacencyList;
    }
}
