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

        HashMap<Integer, Set<Integer>> edges = new HashMap<Integer, Set<Integer>>();

        for (int nodeA = 0; nodeA < numVertices; nodeA++) {
            ArrayList<Integer> nodesB = adjacencyList.get(nodeA);
            Set<Integer> nodesBSet = new HashSet<Integer>(nodesB);
            edges.put(nodeA, nodesBSet);
        }

        for (int nodeA = 0; nodeA < numVertices; nodeA++) {
            Set<Integer> nodesBSet = edges.get(nodeA);
            for (int nodeB : nodesBSet) {
                if (nodeB < nodeA) {
                    for (int nodeC : nodesBSet) {
                        if (nodeA < nodeC && edges.get(nodeB).contains(nodeC)) {
                            triangles.add(new Triangle(nodeB, nodeA, nodeC));
                        }
                    }
                } else {
                    break;
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
