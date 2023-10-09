package seamcarving;

import graphs.Edge;
import graphs.Graph;
import graphs.shortestpaths.DijkstraShortestPathFinder;
import graphs.shortestpaths.ShortestPath;
import graphs.shortestpaths.ShortestPathFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.awt.Point;

public class DijkstraSeamFinder implements SeamFinder {
    private final ShortestPathFinder<Graph<Vertex, Edge<Vertex>>, Vertex, Edge<Vertex>> pathFinder;

    public DijkstraSeamFinder() {
        this.pathFinder = createPathFinder();
    }

    protected <G extends Graph<V, Edge<V>>, V> ShortestPathFinder<G, V, Edge<V>> createPathFinder() {
        /*
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
        return new DijkstraShortestPathFinder<>();
    }
    public class Vertex {
        public double edge;
        public Point location;
        public Vertex(double value, Point location) {
            edge = value;
            this.location = location;
        }
    }

    public class MyGraphHorizontal implements Graph<Vertex, Edge<Vertex>> {
        private HashMap<Point, Vertex> table;
        private HashMap<Vertex, List<Edge<Vertex>>> adjList;

        @Override
        public Collection<Edge<Vertex>> outgoingEdgesFrom(Vertex vertex) {
            Collection<Edge<Vertex>> result = adjList.get(vertex);
            return result;
        }
        public MyGraphHorizontal(double[][] energies, Vertex start, Vertex end) {
            adjList = new HashMap<>();
            table = new HashMap<>();
            adjList.put(start, new ArrayList<>());
            for (int i = 0; i < energies.length; i++) {
                for (int j = 0; j < energies[0].length; j++) {
                    Point location = new Point(j, i);
                    Vertex currVertex = new Vertex(energies[i][j], location);
                    table.put(location, currVertex);
                    adjList.put(currVertex, new ArrayList<>());
                }
            }

            for (int i = 0; i < energies.length; i++) {
                for (int j = 0; j < energies[0].length; j++) {
                    Vertex currVertex = table.get(new Point(j, i));
                    if (j == 0) { // left most
                        Edge<Vertex> edge = new Edge<>(start, currVertex, energies[i][j]);
                        adjList.get(start).add(edge);
                    } else {
                        if (i == 0) { // top row
                            for (int k = 0; k < 2; k++) {
                                Point location = new Point(j - 1, i + k);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energies[i][j]);
                                adjList.get(table.get(location)).add(edge);
                            }
                        } else if (i == energies.length - 1) { // bottom row
                            for (int k = -1; k < 1; k++) {
                                Point location = new Point(j - 1, i + k);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energies[i][j]);
                                adjList.get(table.get(location)).add(edge);
                            }
                        } else {
                            for (int k = -1; k < 2; k++) {
                                Point location = new Point(j - 1, i + k);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energies[i][j]);
                                adjList.get(table.get(location)).add(edge);
                            }
                        }

                        if (j == energies[0].length - 1) { // right most
                            Edge<Vertex> edge = new Edge<>(currVertex, end, 0.0);
                            adjList.get(currVertex).add(edge);
                        }
                    }
                }
            }
        }
    }

    public class MyGraphVertical implements Graph<Vertex, Edge<Vertex>> {
        private HashMap<Point, Vertex> table;
        private HashMap<Vertex, List<Edge<Vertex>>> adjList;
        @Override
        public Collection<Edge<Vertex>> outgoingEdgesFrom(Vertex vertex) {
            Collection<Edge<Vertex>> result = adjList.get(vertex);

            return result;
        }
        public MyGraphVertical(double[][] energies, Vertex start, Vertex end) {
            adjList = new HashMap<>();
            table = new HashMap<>();
            adjList.put(start, new ArrayList<>());

            for (int i = 0; i < energies.length; i++) {
                for (int j = 0; j < energies[0].length; j++) {
                    Point location = new Point(j, i);
                    Vertex currVertex = new Vertex(energies[i][j], location);
                    table.put(location, currVertex);
                    adjList.put(currVertex, new ArrayList<>());
                }
            }

            for (int i = 0; i < energies.length; i++) {
                for (int j = 0; j < energies[0].length; j++) {
                    Vertex currVertex = table.get(new Point(j, i));

                    double energy = energies[i][j];
                    if (i == 0) { // up most
                        Edge<Vertex> edge = new Edge<>(start, currVertex, energy);
                        adjList.get(start).add(edge);
                    } else {
                        if (j == 0) { // left most
                            for (int k = 0; k < 2; k++) {
                                Point location = new Point(j + k, i - 1);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energy);
                                adjList.get(table.get(location)).add(edge);
                            }
                        } else if (j == energies[0].length - 1) { // right most
                            for (int k = -1; k < 1; k++) {
                                Point location = new Point(j + k, i - 1);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energy);
                                adjList.get(table.get(location)).add(edge);
                            }
                        } else {
                            for (int k = -1; k < 2; k++) {
                                Point location = new Point(j + k, i - 1);
                                Edge<Vertex> edge = new Edge<>(table.get(location), currVertex, energy);
                                adjList.get(table.get(location)).add(edge);
                            }
                        }

                        if (i == energies.length - 1) { // bottom
                            Edge<Vertex> edge = new Edge<>(currVertex, end, 0.0);
                            adjList.get(currVertex).add(edge);
                        }
                    }
                }
            }
        }
    }


    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        List<Integer> result = new ArrayList<>();
        Vertex start = new Vertex(0.0, new Point(-1, -1));
        Vertex end = new Vertex(0.0, new Point(-1, -1));
        MyGraphHorizontal g = new MyGraphHorizontal(energies, start, end);
        ShortestPath<Vertex, Edge<Vertex>> path = pathFinder.findShortestPath(g, start, end);
        int verticesCount = 0;
        for (Vertex vertex : path.vertices()) {
            result.add(vertex.location.y);
            verticesCount++;
        }
        result.remove(verticesCount - 1);
        result.remove(0);


        return result;
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        List<Integer> result = new ArrayList<>();
        Vertex start = new Vertex(0.0, new Point(-1, -1));
        Vertex end = new Vertex(0.0, new Point(-1, -1));
        MyGraphVertical g = new MyGraphVertical(energies, start, end);
        ShortestPath<Vertex, Edge<Vertex>> path = pathFinder.findShortestPath(g, start, end);

        int verticesCount = 0;
        for (Vertex vertex : path.vertices()) {
            result.add(vertex.location.x);
            verticesCount++;
        }
        result.remove(verticesCount - 1);
        result.remove(0);

        return result;
    }
}
