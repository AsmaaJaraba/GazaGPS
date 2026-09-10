package com.gazagps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main is the entry point and the coordinator of the app. It builds the map and the
 * control panel, and it owns the current source/target selection. Both the map clicks
 * and the dropdowns report changes here, and Main keeps the two views in sync.
 * data flow (file → GraphData → Dijkstra → Map/ControlPanel)
 */

public class Main extends Application {

    private GraphData graph;
    private Map map;
    private ControlPanel panel;

    private static final Color SOURCE_COLOR = Color.web("#178a60");  // green
    private static final Color TARGET_COLOR = Color.web("#c53b5b");  // red
    private static final Color DEFAULT_COLOR = Color.web("#2766c9");  // normal city blue

    private int source = -1;  // index of chosen source, -1 = none yet
    private int target = -1; // index of chosen target, -1 = none yet

    /**
     * Builds the views, lays them out side by side, and connects the map clicks and
     * the dropdowns to the selection logic.
     */
    @Override
    public void start(Stage stage) {
        graph = new GraphData("gaza.txt");
        map = new Map(graph);
        panel = new ControlPanel(graph);

        BorderPane root = new BorderPane();
        root.setCenter(map.getPane());
        root.setRight(panel.getBox());

        // When a city dot is clicked, treat it as the next selection.
        map.setOnCityClick(index -> selectCity(index));

        // When a dropdown changes, select that city too (keeps map + dropdowns in sync).
        panel.getSourceBox().setOnAction(event -> {
            int i = graph.indexOf(panel.getSourceBox().getValue());
            if (i != -1) setSource(i);
        });
        panel.getTargetBox().setOnAction(event -> {
            int i = graph.indexOf(panel.getTargetBox().getValue());
            if (i != -1) setTarget(i);
        });

        // When Run is clicked, compute and show the shortest route.
        panel.getRunButton().setOnAction(event -> runSearch());
        // When Clear is clicked, reset the whole app back to its starting state.
        panel.getClearButton().setOnAction(event -> clearAll());

        Scene scene = new Scene(root);
        stage.setTitle("Gaza GPS - Shortest Path");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles a click on a city. The first click sets the source, the second sets the
     * target, and a third click starts a fresh selection.
     */
    private void selectCity(int index) {
        if (source == -1) {
            setSource(index);
        } else if (target == -1 && index != source) {
            setTarget(index);
        } else {
            // start over: clear everything, then make this click the new source
            clearSelection();
            setSource(index);
        }
    }

    /**
     * Records the source city. It first resets the previously chosen source dot back
     * to normal (so old selections don't stay coloured), then colours the new dot
     * green and updates the source dropdown to match.
     */
    private void setSource(int index) {
        if (source != -1) {
            map.setCityColor(source, DEFAULT_COLOR);   // un-colour the old source
        }
        source = index;
        map.setCityColor(index, SOURCE_COLOR);
        panel.getSourceBox().setValue(graph.name(index));
    }

    /**
     * Records the target city. It first resets the previously chosen target dot back
     * to normal, then colours the new dot red and updates the target dropdown to match.
     */
    private void setTarget(int index) {
        if (target != -1) {
            map.setCityColor(target, DEFAULT_COLOR);   // un-colour the old target
        }
        target = index;
        map.setCityColor(index, TARGET_COLOR);
        panel.getTargetBox().setValue(graph.name(index));
    }
    private void clearSelection() {
        source = -1;
        target = -1;
        map.resetCityColors();
    }

    /**
     * Runs the shortest-path search for the currently selected source and target.
     * It checks both are chosen, runs Dijkstra on the road matrix, then either shows
     * the route and its distance or reports that no route exists.
     */
    private void runSearch() {
        if (source == -1 || target == -1) {
            panel.setPathText("Please choose both a source and a target.");
            panel.setDistanceText("-");
            return;
        }

        Dijkstra dijkstra = new Dijkstra(graph.matrix());
        dijkstra.run(source, target);

        MyList<Integer> route = dijkstra.pathTo(target);

        if (route.count() == 0) {
            panel.setPathText("No route found between these cities.");
            panel.setDistanceText("-");
            map.clearRoute();
            return;
        }

        map.drawRoute(route);
        panel.setPathText(buildPathText(route));
        panel.setDistanceText(String.format("%.2f km", dijkstra.distanceTo(target)));
    }

    /**
     * Builds the readable path text for the Path box. It lists only the cities on the
     * route and skips any junction nodes, since junctions are bend-points the user
     * doesn't care about. The drawn line and the total distance still include them.
     */
    private String buildPathText(MyList<Integer> route) {
        String text = "";
        boolean first = true;
        for (int i = 0; i < route.count(); i++) {
            int node = route.get(i);
            if (!graph.isCity(node)) continue;
            if (!first) text = text + " -> ";
            text = text + graph.name(node);
            first = false;
        }
        return text;
    }

    /**
     * Resets everything to the starting state: clears the drawn route, removes the
     * source/target selection and their dot colours, empties the dropdowns, and
     * blanks the Path and Distance boxes.
     */
    private void clearAll() {
        map.clearRoute();
        clearSelection();

        panel.getSourceBox().setValue(null);
        panel.getTargetBox().setValue(null);

        panel.setPathText("");
        panel.setDistanceText("-");
    }


    public static void main(String[] args) {
        launch();
    }
}