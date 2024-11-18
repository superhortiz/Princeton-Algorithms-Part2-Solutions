import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The SAP class provides methods to find the shortest ancestral path
 * between two vertices in a digraph.
 */
public class SAP {
    private final Digraph G;
    private final Queue<Integer> queueV, queueW;
    private final Map<Integer, Integer> distToV, distToW;
    private final Set<Integer> visitedV, visitedW;
    private int length, sca;

    /**
     * Constructor that takes a digraph (not necessarily a DAG).
     *
     * @param G the digraph
     * @throws IllegalArgumentException if the digraph is null
     */
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        this.G = new Digraph(G);

        // Initialize data structures for bidirectional BFS
        queueV = new LinkedList<>();
        queueW = new LinkedList<>();
        distToV = new HashMap<>();
        distToW = new HashMap<>();
        visitedV = new HashSet<>();
        visitedW = new HashSet<>();
    }

    /**
     * Returns the length of the shortest ancestral path between v and w.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return the length of the shortest ancestral path; -1 if no such path
     */
    public int length(int v, int w) {
        validate(v, w);
        sca(v, w);
        return length;
    }

    /**
     * Returns a common ancestor of v and w that participates in the shortest ancestral path.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return the common ancestor; -1 if no such path
     */
    public int ancestor(int v, int w) {
        validate(v, w);
        sca(v, w);
        return sca;
    }

    /**
     * Returns the length of the shortest ancestral path between any vertex in v and any vertex in
     * w.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     * @return the length of the shortest ancestral path; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v, w);
        sca(v, w);
        return length;
    }

    /**
     * Returns a common ancestor that participates in the shortest ancestral path between any vertex
     * in v and any vertex in w.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     * @return the common ancestor; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v, w);
        sca(v, w);
        return sca;
    }

    /**
     * Validates the vertices.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @throws IllegalArgumentException if any of the vertices are invalid
     */
    private void validate(int v, int w) {
        if (v < 0 || v >= G.V() || w < 0 || w >= G.V()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Validates the iterables of vertices.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     * @throws IllegalArgumentException if any of the iterables are null or contain invalid vertices
     */
    private void validate(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        for (Integer vertexV : v) {
            if (vertexV == null || vertexV < 0 || vertexV >= G.V())
                throw new IllegalArgumentException();
        }
        for (Integer vertexW : w) {
            if (vertexW == null || vertexW < 0 || vertexW >= G.V())
                throw new IllegalArgumentException();
        }
    }

    /**
     * Helper method to find the shortest common ancestor (sca) using bidirectional BFS.
     *
     * @param v the first vertex
     * @param w the second vertex
     */
    private void sca(int v, int w) {
        if (v == w) {
            length = 0;
            sca = v;
            return;
        }

        // Initialize data structures for bidirectional BFS
        queueV.clear();
        queueW.clear();
        distToV.clear();
        distToW.clear();
        visitedV.clear();
        visitedW.clear();

        // Initialize BFS for starting vertex v
        queueV.add(v);
        distToV.put(v, 0);
        visitedV.add(v);

        // Initialize BFS for starting vertex w
        queueW.add(w);
        distToW.put(w, 0);
        visitedW.add(w);

        sca = -1;
        length = Integer.MAX_VALUE;

        // Perform bidirectional BFS
        bidirectionalBFS();

        // If no common ancestor is found, set length to -1
        if (sca == -1) {
            length = -1;
        }
    }

    /**
     * Helper method to find the shortest common ancestor (sca) for iterables using BFS.
     *
     * @param v the first iterable of vertices
     * @param w the second iterable of vertices
     */
    private void sca(Iterable<Integer> v, Iterable<Integer> w) {
        sca = -1;
        length = Integer.MAX_VALUE;
        Iterator<Integer> iteratorV = v.iterator();
        Iterator<Integer> iteratorW = w.iterator();

        if (!iteratorV.hasNext() || !iteratorW.hasNext()) {
            length = -1;
            return;
        }

        // Initialize data structures for bidirectional BFS
        queueV.clear();
        queueW.clear();
        distToV.clear();
        distToW.clear();
        visitedV.clear();
        visitedW.clear();

        Set<Integer> set = new HashSet<>();

        // Initialize BFS for starting vertex v
        for (int vertexV : v) {
            set.add(vertexV);
            queueV.add(vertexV);
            distToV.put(vertexV, 0);
            visitedV.add(vertexV);
        }

        // Initialize BFS for starting vertex w
        for (int vertexW : w) {
            if (set.contains(vertexW)) {
                length = 0;
                sca = vertexW;
            }
            queueW.add(vertexW);
            distToW.put(vertexW, 0);
            visitedW.add(vertexW);
        }

        // Perform bidirectional BFS
        bidirectionalBFS();

        // If no common ancestor is found, set length to -1
        if (sca == -1) {
            length = -1;
        }
    }

    /**
     * Executes a bidirectional BFS to find the shortest common ancestor (sca) and shortest path
     * length (length).
     * <p>
     * The bidirectional BFS alternates between exploring vertices from the starting vertex v
     * (queueV)
     * and the starting vertex w (queueW). This method continues until either of the queues is
     * empty.
     */
    private void bidirectionalBFS() {
        while (!queueV.isEmpty() || !queueW.isEmpty()) {
            if (!queueV.isEmpty()) {
                bfs(queueV, distToV, distToW, visitedV, visitedW);
            }

            if (!queueW.isEmpty()) {
                bfs(queueW, distToW, distToV, visitedW, visitedV);
            }
        }
    }

    /**
     * Performs one level of BFS from the current vertex queue.
     *
     * @param queue          the current queue of vertices to explore
     * @param distToCurrent  distance map for the current direction of BFS
     * @param distToOther    distance map for the opposite direction of BFS
     * @param visitedCurrent set of visited vertices for the current direction of BFS
     * @param visitedOther   set of visited vertices for the opposite direction of BFS
     */
    private void bfs(Queue<Integer> queue, Map<Integer, Integer> distToCurrent,
                     Map<Integer, Integer> distToOther, Set<Integer> visitedCurrent,
                     Set<Integer> visitedOther) {
        Integer vertex = queue.poll();
        if (vertex == null) return;

        for (int neighbor : G.adj(vertex)) {
            if (!visitedCurrent.contains(neighbor)) {
                queue.add(neighbor);
                visitedCurrent.add(neighbor);
                distToCurrent.put(neighbor, distToCurrent.get(vertex) + 1);
                if (visitedOther.contains(neighbor)) {
                    int totalDist = distToCurrent.get(neighbor) + distToOther.get(neighbor);
                    if (totalDist < length) {
                        length = totalDist;
                        sca = neighbor;
                    }
                }
            }
        }
    }

    /**
     * Unit testing of this class.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        StdOut.println("Reading: " + args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        int v = 2;
        int w = 4;
        int ancestor = sap.ancestor(v, w);
        int length = sap.length(v, w);
        StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
}