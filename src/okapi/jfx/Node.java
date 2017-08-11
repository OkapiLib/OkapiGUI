package okapi.jfx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class Node extends Circle {
    private Coordinate coord;
    private String color;
    private List<Node> neighbors;

    public Node(double radius, Coordinate coord) {
        super(radius);
        neighbors = new ArrayList<>();
        this.coord = coord;
        setStroke(Color.BLACK);
        setCenterX(coord.x);
        setCenterY(coord.y);
        setUnhighlighted();
    }

    public Node(double radius, double x, double y) {
        this(radius, new Coordinate(x, y));
    }

    public void setHighlighted() {
        setStroke(Color.web("0x00000000"));
        setStrokeWidth(3);
    }

    public void setUnhighlighted() {
        setStroke(Color.web("0x00000099"));
        setStrokeWidth(1);
    }

    public void setCenter(double x, double y) {
        setCenterX(x);
        setCenterY(y);
    }

    public void setTranslate(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
    }

    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    public Coordinate getCoord() {
        return coord;
    }

    public String getColor() {
        return color;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return (coord != null ? coord.equals(node.coord) : node.coord == null) &&
               (color != null ? color.equals(node.color) : node.color == null);
    }

    @Override
    public int hashCode() {
        int result = coord != null ? coord.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
