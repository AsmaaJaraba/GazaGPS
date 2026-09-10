# Gaza GPS — Shortest Path

**Design and Analysis of Algorithms (COM336) · Project 3**

A JavaFX desktop application that finds the shortest route between two localities
in the Gaza Strip using **Dijkstra's algorithm**, and draws that route on a real
map. The user picks a source and target — by clicking a city on the map or using
the dropdowns — and the app computes the cheapest path, follows the real roads,
and reports the total distance.

---

## Features

- **30 real localities** across the Gaza Strip, with real latitude/longitude coordinates.
- **Dijkstra's shortest-path algorithm** implemented from scratch (the `known` / `dist` /
  `path` table with a linear scan for the minimum, matching the course notes).
- **Distances computed at runtime** with the **Haversine** (great-circle) formula —
  never stored in the input file.
- **Real-road routing:** routes follow the actual streets rather than straight lines,
  using junction nodes placed along the roads.
- **Two ways to select cities:** click a dot on the map, or use the Source/Target dropdowns
  (the two stay in sync).
- **Route highlighting:** the shortest path is drawn in red on the map, with the city list
  and total distance shown in the side panel.
- **Clear** button resets the selection, the route, and the output boxes.

---

## How to run

1. Open the project in **IntelliJ IDEA** (it is a JavaFX + Maven project — the JavaFX
   libraries are pulled automatically by Maven).
2. Run the `Main` class.
3. The map and city data load from `gaza.txt` at the project root.

---

## Project structure

| Class          | Responsibility                                                                 |
|----------------|--------------------------------------------------------------------------------|
| `Main`         | Entry point and coordinator: owns the selection, wires clicks and dropdowns, runs the search. |
| `GraphData`    | Loads `gaza.txt` into parallel arrays and an adjacency matrix; computes Haversine distances. |
| `Dijkstra`     | The shortest-path algorithm: `known` / `dist` / `path` arrays, early stop, route reconstruction. |
| `MapView`      | Draws the map image, city circles, junctions, and the highlighted route.       |
| `ControlPanel` | The side panel: Source/Target dropdowns, Run, Clear, Path and Distance boxes.   |
| `MyList`       | A generic list implemented from scratch (used for the route lines).            |

Data flows one direction:
`gaza.txt` → **GraphData** → **Dijkstra** → **MapView** + **ControlPanel**, with **Main** orchestrating.

---

## Input file format (`gaza.txt`)

```
N E C
<N lines>  name  latitude  longitude
<E lines>  nameA  nameB
```

- `N` — total number of nodes (cities + road junctions)
- `E` — number of edges (road segments)
- `C` — how many of the nodes are real cities (listed first); the rest are junctions

Road distances are **not** stored — they are computed at load time from the
coordinates using the Haversine formula. Junctions are extra points along the
roads that let the drawn route bend to follow real streets.

---

## Notes

- The graph is **undirected**: each road is stored in both directions of the matrix.
- The algorithm runs in **O(V²)** using a linear scan for the minimum, which is
  well suited to a map of this size and matches the course material.
- Coordinates are projected onto the map image using its geographic bounds, with the
  y-axis flipped so north stays at the top.
