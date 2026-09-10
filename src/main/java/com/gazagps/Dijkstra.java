package com.gazagps;

/**
 * Dijkstra implements the classic shortest-path algorithm using three parallel arrays (known, dist, path) and a linear
 * scan to choose the next closest unknown vertex (the O(V^2) version, no priority queue).
 * It also stops early as soon as the requested target is finalized.
 *
 * It runs on a weight matrix where adj[i][j] is the road length from i to j, and a
 * value of 0.0 means "no direct road". The matrix is only read, never modified.
 */
public class Dijkstra {

    private static final double INF = Double.POSITIVE_INFINITY;

    private double[][] adj; // the weight matrix (road lengths)
    private int n; // number of vertices
    private boolean[] known; // known[v]  = true once v's shortest distance is final
    private double[] dist; // dist[v]   = best known distance from source to v
    private int[] path; // path[v]   = the vertex we came from to reach v (-1 = none)

    /**
     * Stores the weight matrix the algorithm will run on and remembers its size.
     */
    public Dijkstra(double[][] adj) {
        this.adj = adj;
        this.n = adj.length;
    }

    /**
     * Runs Dijkstra from the given source. It repeatedly picks the closest unknown
     * vertex, marks it known, and relaxes its neighbours. It stops as soon as the
     * target is finalized, since that vertex's distance can no longer improve.
     */
    public void run(int source, int target) {
        known = new boolean[n];
        dist  = new double[n];
        path  = new int[n];

        for (int i = 0; i < n; i++) {
            known[i] = false;
            dist[i]  = INF;
            path[i]  = -1;
        }
        dist[source] = 0;

        while (true) {
            int v = smallestUnknownVertex();
            if (v == -1) {
                break; // no reachable unknown vertex left
            }
            known[v] = true;
            if (v == target) {
                break;  // early stop: target's distance is now final
            }

            for (int w = 0; w < n; w++) {
                if (adj[v][w] > 0 && !known[w]) {
                    if (dist[v] + adj[v][w] < dist[w]) {
                        dist[w] = dist[v] + adj[v][w];
                        path[w] = v;
                    }
                }
            }
        }
    }

    /**
     * Scans every vertex and returns the unknown one with the smallest distance.
     * Returns -1 if no unknown vertex is reachable (all remaining distances are
     * still infinity). This linear scan is our replacement for a priority queue.
     */
    private int smallestUnknownVertex() {
        int min = -1;
        double best = INF;
        for (int i = 0; i < n; i++) {
            if (!known[i] && dist[i] < best) {
                best = dist[i];
                min = i;
            }
        }
        return min;
    }

    /**
     * Returns the total shortest distance from the source to the given target,
     * or infinity if the target was never reached.
     */
    public double distanceTo(int target) {
        return dist[target];
    }

    /**
     * Rebuilds the shortest route to the target as a list of node indices, ordered
     * from source to target. It walks the predecessor array path[] backwards from
     * the target, inserting each node at the front of the list so the final order
     * comes out source-to-target. Returns an empty list if the target is unreachable.
     */
    public MyList<Integer> pathTo(int target) {
        MyList<Integer> route = new MyList<>(n);   // n = number of nodes (safe capacity)

        if (dist[target] == INF) {
            return route;  // unreachable: empty list
        }

        // Walk backwards from target to source, adding each node to the FRONT.
        for (int v = target; v != -1; v = path[v]) {
            route.insertAt(0, v);
        }

        return route;
    }

}