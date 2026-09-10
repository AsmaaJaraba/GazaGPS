package com.gazagps;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.InputStream;

/**
 * Map builds the visual side of the app: the Gaza map image as a background,
 * a circle for every city placed at its real geographic position, and a line for
 * every road. It converts each city's latitude/longitude into a pixel position
 * using the map image's known geographic bounds, then draws onto a Pane.
 */
public class Map {

    public static final double MAP_WIDTH  = 700;
    public static final double MAP_HEIGHT = 846;

    private static final double LAT_MAX = 31.5978;   // top edge of the image
    private static final double LAT_MIN = 31.2210;   // bottom edge
    private static final double LON_MIN = 34.2025;   // left edge
    private static final double LON_MAX = 34.5685;   // right edge

    private GraphData graph;
    private Pane pane;

    private double[] cityX;
    private double[] cityY;
    private Circle[] cityCircle;
    private Text[] cityLabel;
    private Color defaultCityColor = Color.web("#2766c9");
    private MyList<Line> routeLines;// remembers the highlight lines of the current route so we can remove them next run

    /**
     * Builds the complete map view from the given graph: it projects every city
     * to pixels, then draws the background, the roads, and the cities in order.
     */
    public Map(GraphData graph) {
        this.graph = graph;
        this.pane = new Pane();
        this.pane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);

        projectCities();
        drawBackground();
       // drawRoads();
        drawJunctions();
        drawCities();
    }

    /**
     * Converts a longitude into an x pixel position across the width of the map.
     */
    private double lonToX(double lon) {
        return (lon - LON_MIN) / (LON_MAX - LON_MIN) * MAP_WIDTH;
    }

    /**
     * Converts a latitude into a y pixel position. It subtracts the latitude from
     * LAT_MAX because latitude increases upward while screen y increases downward;
     * without this flip the whole map would be drawn upside-down.
     */
    private double latToY(double lat) {
        return (LAT_MAX - lat) / (LAT_MAX - LAT_MIN) * MAP_HEIGHT;
    }

    /**
     * Computes the pixel position of every city once and stores them, so the
     * roads, the circles, the mouse handling all use the same points.
     */
    private void projectCities() {
        int n = graph.size();
        cityX = new double[n];
        cityY = new double[n];
        for (int i = 0; i < n; i++) {
            cityX[i] = lonToX(graph.lon(i));
            cityY[i] = latToY(graph.lat(i));
        }
    }

    /**
     * Loads gaza_map.png from the resources folder and adds it as the background,
     * scaled to the display size. Prints a clear message if the image is missing.
     */
    private void drawBackground() {
        InputStream in = getClass().getResourceAsStream("/gaza_map.png");
        if (in == null) {
            System.out.println("ERROR: gaza_map.png not found in resources.");
            return;
        }
        ImageView view = new ImageView(new Image(in));
        view.setFitWidth(MAP_WIDTH);
        view.setFitHeight(MAP_HEIGHT);
        pane.getChildren().add(view);
    }

    /**
     * Draws one line per road. Because the adjacency matrix is symmetric, it only
     * checks pairs where i is less than j, so each road is drawn exactly once.
     */
    private void drawRoads() {
        int n = graph.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.weight(i, j) > 0) {
                    Line road = new Line(cityX[i], cityY[i], cityX[j], cityY[j]);
                    road.setStroke(Color.web("#5b6b7a"));
                    road.setStrokeWidth(2);
                    pane.getChildren().add(road);
                }
            }
        }
    }

    /**
     * Draws a circle and a permanent name label for every city, on top of the roads.
     * When the mouse hovers a city, that city's dot and label grow, turn bold and
     * change colour, and jump to the front, so the highlighted city stands out
     * clearly even where cities are clustered close together. The circles and labels
     * are stored so they can also be recoloured later for selection and path highlighting.
     */
    private void drawCities() {
        int n = graph.cityCount();
        cityCircle = new Circle[n];
        cityLabel  = new Text[n];

        for (int i = 0; i < n; i++) {
            Circle c = new Circle(cityX[i], cityY[i], 6);
            c.setFill(Color.web("#2766c9"));
            c.setStroke(Color.WHITE);
            c.setStrokeWidth(2);

            Text label = new Text(cityX[i] + 8, cityY[i] + 4, graph.name(i));
            label.setFont(Font.font(10));
            label.setFill(Color.web("#1b2a38"));

            final int index = i;   // fixed copy of i so each handler knows its own city

            c.setOnMouseEntered(event -> highlightCity(index, true));
            c.setOnMouseExited(event -> highlightCity(index, false));
            label.setOnMouseEntered(event -> highlightCity(index, true));
            label.setOnMouseExited(event -> highlightCity(index, false));

            pane.getChildren().add(c);
            pane.getChildren().add(label);
            cityCircle[i] = c;
            cityLabel[i]  = label;
        }
    }

    /**
     * Makes one city stand out (or returns it to normal). When highlighted, the dot
     * grows and the label becomes bigger, bold, recoloured, and lifted to the front
     * so it is readable above any overlapping neighbours.
     */
    private void highlightCity(int index, boolean on) {
        Circle c = cityCircle[index];
        Text label = cityLabel[index];

        if (on) {
            c.setRadius(9);
            label.setFont(Font.font("System", FontWeight.BOLD, 14));
            c.toFront();
            label.toFront();
        }
        else {
            c.setRadius(6);
            label.setFont(Font.font(10));
            label.setFill(Color.web("#1b2a38"));
        }
    }

    /**
     * Returns the finished pane so Main can place it inside the window's scene.
     */
    public Pane getPane() {
        return pane;
    }

    /**
     * Sets the fill colour of a single city's dot. Used to mark the chosen source
     * and target, and to reset cities back to their normal colour.
     */
    public void setCityColor(int index, Color color) {
        cityCircle[index].setFill(color);
    }

    /**
     * Resets every city's dot back to the default colour, clearing any source,
     * target, or path highlighting from a previous run.
     */
    public void resetCityColors() {
        for (int i = 0; i < cityCircle.length; i++) {
            cityCircle[i].setFill(defaultCityColor);
        }
    }

    /**
     * Registers an action to run when a city dot is clicked, handing back the index
     * of the clicked city. The main app uses this to drive source/target selection.
     */
    public void setOnCityClick(java.util.function.IntConsumer handler) {
        for (int i = 0; i < cityCircle.length; i++) {
            final int index = i;
            cityCircle[i].setOnMouseClicked(event -> handler.accept(index));
        }
    }

    /**
     * Draws the shortest route as thick red lines on top of the map. It connects each
     * node in the route to the next one (cities and junctions alike), so the line bends
     * along the road, and remembers every line it draws so the route can be cleared
     * before the next run.
     */
    public void drawRoute(MyList<Integer> route) {
        clearRoute();
        routeLines = new MyList<>(route.count());   // at most route.count()-1 lines

        for (int k = 0; k < route.count() - 1; k++) {
            int a = route.get(k);
            int b = route.get(k + 1);

            Line line = new Line(cityX[a], cityY[a], cityX[b], cityY[b]);
            line.setStroke(Color.web("#c53b5b"));
            line.setStrokeWidth(4);
            pane.getChildren().add(line);

            routeLines.add(line);



        }
    }


    /**
     * Removes the lines of any previously drawn route from the map, so a new run starts
     * from a clean map. Safe to call even when no route is currently drawn.
     */
    public void clearRoute() {
        if (routeLines == null) {
            return;
        }
        for (int i = 0; i < routeLines.count(); i++) {
            Line line = routeLines.get(i);
            if (line != null) {
                pane.getChildren().remove(line);
            }
        }
        routeLines.clear();
    }

    /**
     * Draws each road junction as a small, unlabelled grey dot. Junctions are only
     * bend-points that make routes follow the streets; they are not selectable, so
     * they get no label, no hover effect, and no click handler.
     */
    private void drawJunctions() {
        for (int i = graph.cityCount(); i < graph.size(); i++) {
            Circle dot = new Circle(cityX[i], cityY[i], 2);
            dot.setFill(Color.web("#9aa7b5"));
            pane.getChildren().add(dot);
        }
    }

}