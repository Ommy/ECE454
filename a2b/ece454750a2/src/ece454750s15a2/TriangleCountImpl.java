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
        List<Triangle> triangles = enumerateTrianglesThreaded();
        System.out.println("Number of triangles found: " + triangles.size());
        return triangles;
    }

    private List<Triangle> enumerateTrianglesThreaded() throws IOException, InterruptedException, ExecutionException {

        final List<Triangle> triangles = new ArrayList<Triangle>();

        Graph graph = new Graph(input);

        try {
            long beginTime = System.nanoTime();

            graph.readParameters();

            List<Integer>[] biggerNeighbours = new ArrayList[graph.numVertices];

            for (int i = 0; i < graph.numVertices; i++) {
                biggerNeighbours[i] = new ArrayList<Integer>();
            }

            final ExecutorService runnableExecutorService = Executors.newFixedThreadPool(numCores);

            String line = null;
            while ((line = graph.bufferedReader.readLine()) != null && !line.equals("")) {
                runnableExecutorService.execute(new ParseInputEdgesRunnable(line, biggerNeighbours));
            }

            runnableExecutorService.shutdown();
            runnableExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            long parseTime = System.nanoTime();
            System.out.println("Parse time     : " + (parseTime - beginTime));

            final List<Callable<List<Triangle>>> callables = new ArrayList<Callable<List<Triangle>>>();
            for (int i = 0; i < numCores; i++) {
                callables.add(new EnumerateTriangleCallable(i, numCores, biggerNeighbours));
            }

            final ExecutorService callableExecutorService = Executors.newFixedThreadPool(numCores);
            final List<Future<List<Triangle>>> futures = new ArrayList<Future<List<Triangle>>>();
            for (int i = 0; i < numCores; i++) {
                futures.add(callableExecutorService.submit(callables.get(i)));
            }

            for (Future<List<Triangle>> future : futures) {
                triangles.addAll(future.get());
            }

            long finishTime = System.nanoTime();
            System.out.println("Triangle time  : " + (finishTime - parseTime));
            System.out.println("Total time     : " + (finishTime - beginTime));

        } finally {
            graph.close();
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
            String parts[] = line.split(": ", 2);

            final Integer vertex = Integer.parseInt(parts[0]);

            final ArrayList<Integer> bigNeighbours = new ArrayList<Integer>();

            if (parts.length > 1) {
                StringTokenizer tokenizer = new StringTokenizer(parts[1]);
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
            final List<Triangle> triangles = new ArrayList<Triangle>();

            for (int smallVertex = initialPosition; smallVertex < biggerNeighbours.length; smallVertex += step) {

                List<Integer> smallNeighbours = biggerNeighbours[smallVertex];

                if (smallNeighbours == null) {
                    continue;
                }

                for (int mediumVertex : smallNeighbours) {

                     List<Integer> mediumNeighbours = biggerNeighbours[mediumVertex];

                    if (mediumNeighbours == null) {
                        continue;
                    }

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
    }

    public List<Triangle> enumerateTrianglesTest() throws IOException, InterruptedException
    {
        List<Integer>[] adjacencyLists = getAdjacencyLists();
        ExecutorService executor = Executors.newFixedThreadPool(numCores);
        List<Triangle> triangles = Collections.synchronizedList(new ArrayList<Triangle>());

        for(int i = 0; i < numCores; i++)
        {
            Worker worker = new Worker(adjacencyLists, i, triangles);
            executor.execute(worker);
        }

        executor.shutdown();
        executor.awaitTermination(2L, TimeUnit.MINUTES);

        return triangles;
    }

    private class Worker implements Runnable
    {
        private final List<Integer>[] _adjacencyLists;
        private final int _startingIndex;
        private final List<Triangle> _triangles;

        Worker(List<Integer>[] adjacencyLists,int startingIndex, List<Triangle> triangles)
        {
            _adjacencyLists = adjacencyLists;
            _startingIndex = startingIndex;
            _triangles = triangles;
        }

        @Override
        public void run()
        {
            List<Triangle> localTriangles = new ArrayList<Triangle>();

            for(int firstVertex = _startingIndex; firstVertex < _adjacencyLists.length; firstVertex += numCores)
            {
                List<Integer> smallerEdges = _adjacencyLists[firstVertex];

                if(null == smallerEdges)
                {
                    continue;
                }

                for(int secondVertex : smallerEdges)
                {
                    List<Integer> mediumEdges = _adjacencyLists[secondVertex];

                    if(null == mediumEdges)
                    {
                        continue;
                    }

                    int i_s = smallerEdges.size() - 1;
                    int i_m = mediumEdges.size() - 1;
                    while(i_s >= 0 && i_m >= 0)
                    {
                        int check_s = smallerEdges.get(i_s);
                        int check_m = mediumEdges.get(i_m);
                        if(check_s == check_m)
                        {
                            localTriangles.add(new Triangle(firstVertex, secondVertex, check_s));
                            --i_m;
                            --i_s;
                        }
                        else if(check_s > check_m)
                        {
                            --i_s;
                        }
                        else
                        {
                            if(check_s < secondVertex)
                            {
                                break;
                            }
                            --i_m;
                        }
                    }
                }
            }

            _triangles.addAll(localTriangles);
        }
    }

    private List<Integer>[] getAdjacencyLists() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input)));

        try
        {
            String headerLine = reader.readLine();

            if (!headerLine.contains("vertices") || !headerLine.contains("edges"))
            {
                System.err.println("Invalid graph file format. Offending line: " + headerLine);
                System.exit(-1);
            }

            String parts[] = headerLine.split(", ");
            int numVertices = Integer.parseInt(parts[0].split(" ")[0]);
            int numEdges = Integer.parseInt(parts[1].split(" ")[0]);
            System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");

            return getAdjacencyLists(reader, numVertices);
        }
        finally
        {
            reader.close();
        }
    }

    private List<Integer>[] getAdjacencyLists(BufferedReader reader, int numVertices) throws IOException
    {
        List<Integer>[] adjacencyLists = new List[numVertices];
        String line;

        while(null != (line = reader.readLine()) && !line.isEmpty())
        {
            String[] vertexDescriptor = line.split(": ");

            // If the vertex has no adjacent vertices
            if(vertexDescriptor.length <= 1)
            {
                continue;
            }

            int vertexID = Integer.parseInt(vertexDescriptor[0]);
            String[] adjacentVertices = vertexDescriptor[1].split(" ");

            // If the vertex has less than 2 adjacent vertices, then it can't be a part of a triangle
            if(adjacentVertices.length <= 1)
            {
                continue;
            }

            List<Integer> adjacentVerticesSet = new ArrayList<Integer>(adjacentVertices.length);

            for(String adjacentVertex : adjacentVertices)
            {
                int adjacentVertexID = Integer.parseInt(adjacentVertex);

                if(adjacentVertexID > vertexID)
                {
                    adjacentVerticesSet.add(adjacentVertexID);
                }
            }

            if(!adjacentVerticesSet.isEmpty())
            {
                Collections.sort(adjacentVerticesSet);
                adjacencyLists[vertexID] = adjacentVerticesSet;
            }
        }

        return adjacencyLists;
    }

}
