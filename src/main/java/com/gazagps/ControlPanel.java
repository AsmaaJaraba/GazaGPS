package com.gazagps;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * ControlPanel is the side panel beside the map. It holds the Source and Target
 * dropdowns, the Run button, and the read-only Path and Distance boxes shown in the
 * project's example interface. This class only builds and arranges the controls;
 * the actual route-finding is wired up later in the main app.
 */
public class ControlPanel {

    private VBox box;

    private ComboBox<String> sourceBox;
    private ComboBox<String> targetBox;
    private Button runButton;
    private TextArea pathArea;
    private Label distanceValue;
    private Button clearButton;

    /**
     * Builds the whole panel from the graph: it fills both dropdowns with the city
     * names and lays out every control vertically in display order.
     */
    public ControlPanel(GraphData graph) {
        box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(230);
        box.setStyle("-fx-background-color: #f4f6fa;");

        Label title = new Label("Gaza GPS");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Collect the 30 city names into the kind of list a ComboBox accepts.
        String[] names = new String[graph.cityCount()];
        for (int i = 0; i < graph.cityCount(); i++) {
            names[i] = graph.name(i);
        }

        sourceBox = new ComboBox<>(FXCollections.observableArrayList(names));
        sourceBox.setPromptText("Choose source");
        sourceBox.setMaxWidth(Double.MAX_VALUE); // stretch to the panel width

        targetBox = new ComboBox<>(FXCollections.observableArrayList(names));
        targetBox.setPromptText("Choose target");
        targetBox.setMaxWidth(Double.MAX_VALUE);

        runButton = new Button("Run");
        runButton.setMaxWidth(Double.MAX_VALUE);
        runButton.setStyle("-fx-background-color: #C62828; -fx-text-fill: white; -fx-font-weight: bold;");

        clearButton = new Button("Clear");
        clearButton.setMaxWidth(Double.MAX_VALUE);
        clearButton.setStyle("-fx-background-color: #e7edf5; -fx-text-fill: #1b2a38;");

        Label pathLabel = new Label("Path:");
        pathArea = new TextArea();
        pathArea.setEditable(false);
        pathArea.setWrapText(true);
        pathArea.setPrefRowCount(4);

        Label distanceLabel = new Label("Distance:");
        distanceValue = new Label("-");
        distanceValue.setFont(Font.font("System", FontWeight.BOLD, 14));

        box.getChildren().addAll(title, new Label("Source:"), sourceBox, new Label("Target:"), targetBox, runButton,clearButton, pathLabel, pathArea, distanceLabel, distanceValue);
    }


    public VBox getBox() {
        return box;
    }

    public ComboBox<String> getSourceBox() {
        return sourceBox;
    }

    public ComboBox<String> getTargetBox() {
        return targetBox;
    }

    /**
     * Returns the Run button so the app can attach the route-finding action to it.
     */
    public Button getRunButton() {
        return runButton;
    }

    public void setPathText(String text) {
        pathArea.setText(text);
    }

    public void setDistanceText(String text) {
        distanceValue.setText(text);
    }

    public Button getClearButton() {
        return clearButton;
    }
}