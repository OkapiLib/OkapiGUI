package okapi.jfx;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class LineFactory {
    public static Line getLine(Coordinate from, Coordinate to) {
        Line line = new Line(from, to);
        line.setStrokeWidth(3);
        return line;
    }

    public static Line getLine(Node from, Node to) {
        return getLine(from.getCoord(), to.getCoord());
    }
}
