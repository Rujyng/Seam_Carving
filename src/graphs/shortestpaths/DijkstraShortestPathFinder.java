package graphs.shortestpaths;

import priorityqueues.ExtrinsicMinPQ;
import priorityqueues.NaiveMinPQ;
import graphs.BaseEdge;
import graphs.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see SPTShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    extends SPTShortestPathFinder<G, V, E> {

    @SuppressWarnings("checkstyle:CommentsIndentation")
    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new NaiveMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
         */

        //return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
        */
}

    // Returns a (partial) shortest paths tree (a map from vertex to preceding edge)
    // containing the shortest path from start to end in given graph.
    @Override
    protected Map<V, E> constructShortestPathsTree(G graph, V start, V end) {
        ExtrinsicMinPQ<V> known = createMinPQ();
        Map<V, E> edgeTo = new HashMap<>();
        Map<V, Double> distTo = new HashMap<>();
        distTo.put(start, 0.0);
        known.add(start, 0);

        while (!known.isEmpty()) {
            V u = known.removeMin();
            if (u.equals(end)) {
                break;
            }
            Collection<E> connectedEdges = graph.outgoingEdgesFrom(u);
            for (E edge : connectedEdges) {
                V v = edge.to();
                Double w = edge.weight();
                if (!distTo.containsKey(v)) {
                    distTo.put(v, Double.POSITIVE_INFINITY);
                }
                Double oldDist = distTo.get(v);
                Double newDist = distTo.get(u) + w;

                if (newDist < oldDist) {
                    distTo.put(v, newDist);
                    edgeTo.put(v, edge);
                    if (known.contains(v)) {
                        known.changePriority(v, newDist);
                    } else {
                        known.add(v, newDist);
                    }
                }
            }
        }
        return edgeTo;
    }

    // Extracts the shortest path from start to end from the given shortest paths tree.
    @Override
    protected ShortestPath<V, E> extractShortestPath(Map<V, E> spt, V start, V end) {
        if (start.equals(end)) {
            return new ShortestPath.SingleVertex<>(end);
        }
        List<E> path = new ArrayList<>();
        V curNode = end;
        path.add(spt.get(end));
        if (!spt.containsKey(end)) {
            return new ShortestPath.Failure<>();
        }
        while (!curNode.equals(start) && spt.get(curNode) != null) {
            curNode = spt.get(curNode).from();
            path.add(spt.get(curNode));
        }
        Collections.reverse(path);
        path.remove(0);
        return new ShortestPath.Success<>(path);


        // ShortestPath<V, E> result = new ShortestPath<V, E>() {
        //
        //     // this is checking if the start vertex given is connected to the end vertex
        //     @Override
        //     public boolean exists() {
        //         Stack<V> perimeter = new Stack<>();
        //         Set<V> visited = new HashSet<>();
        //         perimeter.add(start);
        //
        //         while (!perimeter.isEmpty()) {
        //             V from = perimeter.pop();
        //             if (!visited.contains(from)) {
        //                 for (V n : spt.keySet()) { // check every vertex
        //                     if (spt.get(n).from() == from) { // check for neighbor of from
        //                         V to = n;
        //                         if (to == end) {
        //                             return true;
        //                         }
        //                         perimeter.add(to);
        //                     }
        //                 }
        //                 visited.add(from);
        //             }
        //         }
        //         return false;
        //     }
        //     // public boolean exists() {
        //     //     visited.add(start);
        //     //     return connected(start, end);
        //     // }
        //     //
        //     // private boolean connected(V s, V t) { // A -- B -- C
        //     //     if (s == t) {
        //     //         return true;
        //     //     } else {
        //     //         visited.add(s); // A B
        //     //         for (V n : spt.keySet()) {
        //     //             if (spt.get(n).from() == s && // A == A
        //     //                 !visited.contains(n) && // AB doesn't contain C
        //     //                 connected(n, t)) { // C, C
        //     //                     return true;
        //     //             }
        //     //         }
        //     //         return false;
        //     //     }
        //     // }
        //
        //     // use dfs or bfs but keep track of previous nodes if path reaches an endpoint and is not the end vertex
        //     // recursive backtracking
        //
        //     @Override
        //     public List<E> edges() {
        //         Stack<V> perimeter = new Stack<>();
        //         Set<V> visited = new HashSet<>();
        //         List<E> result = new ArrayList<>();
        //         boolean noNeighbors = true;
        //         perimeter.add(start);
        //
        //         while (!perimeter.isEmpty()) {
        //             V from = perimeter.pop();
        //             result.add(spt.get(from));
        //             if (!visited.contains(from)) {
        //                 for (V n : spt.keySet()) { // check every vertex
        //                     if (spt.get(n).from() == from) { // check for neighbor of from
        //                         noNeighbors = false;
        //                         perimeter.add(n);
        //                     }
        //                 }
        //                 if (noNeighbors && from != end) {
        //                     result.clear();
        //                 } else if (from == end) {
        //                     result.remove(0);
        //                     return result;
        //                 }
        //                 noNeighbors = true;
        //                 visited.add(from);
        //             }
        //         }
        //         return result;
        //     }
        //     // public List<E> edges() {
        //     //     List<E> result = new ArrayList<>();
        //     //     List<E> temp = new ArrayList<>();
        //     //
        //     //
        //     //     if (!exists()) {
        //     //         return result;
        //     //     }
        //     //     edgesHelper(start, end, temp, result);
        //     //     return result;
        //     // }
        //     //
        //     // private List<E> edgesHelper(V s, V t, List<E> temp, List<E> result) {
        //     //     if (s == t) {
        //     //         for (int i = 0; i < temp.size(); i++) {
        //     //             result.add(temp.get(i));
        //     //         }
        //     //         return result;
        //     //     } else {
        //     //         for (V n : spt.keySet()) {
        //     //             if (spt.get(n).from() == s) { // check neighbors
        //     //                 temp.add(spt.get(n));
        //     //                 edgesHelper(n, end, temp, result);
        //     //                 temp.remove(spt.get(n));
        //     //             }
        //     //         }
        //     //         return result;
        //     //     }
        //     // }
        //
        //     @Override
        //     public List<V> vertices() {
        //         Stack<V> perimeter = new Stack<>();
        //         Set<V> visited = new HashSet<>();
        //         List<V> result = new ArrayList<>();
        //         boolean noNeighbors = true;
        //         perimeter.add(start);
        //
        //         while (!perimeter.isEmpty()) {
        //             V from = perimeter.pop();
        //             result.add(from);
        //             if (!visited.contains(from)) {
        //                 for (V n : spt.keySet()) { // check every vertex
        //                     if (spt.get(n).from() == from) { // check for neighbor of from
        //                         noNeighbors = false;
        //                         perimeter.add(n);
        //                     }
        //                 }
        //                 if (noNeighbors && from != end) {
        //                     result.clear();
        //                     result.add(start);
        //                 } else if (from == end) {
        //                     return result;
        //                 }
        //                 noNeighbors = true;
        //                 visited.add(from);
        //             }
        //         }
        //         return result;
        //     }
        //     //     List<V> result = new ArrayList<>();
        //     //     List<V> temp = new ArrayList<>();
        //     //
        //     //     if (!exists()) {
        //     //         return result;
        //     //     }
        //     //     result.add(start);
        //     //     verticesHelper(start, end, temp, result);
        //     //     return result;
        //     // }
        //     //
        //     // private List<V> verticesHelper(V s, V t, List<V> temp, List<V> result) {
        //     //     if (s == t) {
        //     //         for (int i = 0; i < temp.size(); i++) {
        //     //             result.add(temp.get(i));
        //     //         }
        //     //         return result;
        //     //     } else {
        //     //         for (V n : spt.keySet()) {
        //     //             if (spt.get(n).from() == s) { // check neighbors
        //     //                 temp.add(n);
        //     //                 verticesHelper(n, end, temp, result);
        //     //                 temp.remove(n);
        //     //             }
        //     //         }
        //     //         return result;
        //     //     }
        //     // }
        //
        // };
    }
}
