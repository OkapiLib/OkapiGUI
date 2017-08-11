package okapi.jfx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

public class Node extends Circle {
    private Pair<Double, Double> coord;
    private String color;

    public Node(double radius, Pair<Double, Double> coord) {
        super(radius);
        this.coord = coord;
    }

    public void setHighlighted() {
        setStroke(Color.web("0x00000000"));
        setStrokeWidth(3);
    }

    public void setUnhighlighted() {
        setStroke(Color.web("0x00000099"));
        setStrokeWidth(1);
    }

    public Pair<Double, Double> getCoord() {
        return coord;
    }

    public String getColor() {
        return color;
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
