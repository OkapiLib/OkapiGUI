package okapi.jfx;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class Line extends javafx.scene.shape.Line {
    public Coordinate from, to;

    public Line(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }
}
