package com.gazagps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * GraphData holds the entire map in memory.
 *
 * It reads gaza.txt into parallel arrays (one entry per city) and a 2D
 * adjacency matrix for the roads. Road distances are NOT stored in the file;
 * they are computed at runtime with the Haversine formula when the file loads.
 * A value of 0.0 in the matrix means "no direct road between these two cities".
 */
public class GraphData {

    private int V;
    private int E;
    private int cityCount;
    private String[] cityName;
    private double[] cityLat;
    private double[] cityLon;
    private double[][] adj;    // adj[i][j] = road length in km, or 0.0 if no road


    public GraphData(String filename) {
        load(filename);
    }

    /**
     * Reads the whole file: first the city/road counts, then every city's
     * name and coordinates, then every road. Each road's distance is computed
     * with haversine() and written into both directions of the matrix.
     */
    private void load(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));

            V = Integer.parseInt(sc.next());
            E = Integer.parseInt(sc.next());
            cityCount = Integer.parseInt(sc.next());

            cityName = new String[V];
            cityLat = new double[V];
            cityLon = new double[V];
            adj = new double[V][V];

            for (int i = 0; i < V; i++) {
                cityName[i] = sc.next();
                cityLat[i] = Double.parseDouble(sc.next());
                cityLon[i] = Double.parseDouble(sc.next());
            }

            for (int e = 0; e < E; e++) {
                String first = sc.next();
                String second = sc.next();

                int i = indexOf(first);
                int j = indexOf(second);

                double distance = haversine(cityLat[i], cityLon[i], cityLat[j], cityLon[j]);

                adj[i][j] = distance;
                adj[j][i] = distance;
            }

            sc.close();
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: could not find the file '" + filename + "'");
        }
    }

    /**
     * Finds a city's array index by its name using a linear search.
     * This is our replacement for a HashMap. Returns -1 if not found.
     */
    public int indexOf(String name) {
        for (int i = 0; i < V; i++) {
            if (cityName[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the great-circle distance in kilometres between two
     * latitude/longitude points using the Haversine formula.
     */
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


    public int size() { return V; }

    public int edgeCount() { return E; }

    public String name(int i) { return cityName[i]; }

    public double lat(int i) { return cityLat[i]; }

    public double lon(int i) { return cityLon[i]; }

    /**
     * Returns the road length in km between cities i and j, or 0.0 if there
     * is no direct road connecting them.
     */
    public double weight(int i, int j) { return adj[i][j]; }

    /**
     * Returns the adjacency matrix of road lengths so the Dijkstra class can read
     * the edge weights. Dijkstra only reads it and never changes it.
     */
    public double[][] matrix() { return adj; }

    public int cityCount() { return cityCount; }

    public boolean isCity(int i) { return i < cityCount; }
}