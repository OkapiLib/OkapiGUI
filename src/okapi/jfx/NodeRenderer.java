package okapi.jfx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class NodeRenderer {
    /**
     * Draws lines between locations in the order in their list
     *
     * @param points Ordered list of locations to draw lines between
     * @return       Output lines
     */
    public static List<Line> drawLinesInOrder(List<Coordinate> points)
    {
        List<Line> out = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++)
        {
            Coordinate current = points.get(i),
                    next = points.get(i + 1);
            out.add(LineFactory.getLine(current, next));
        }
        return out;
    }

    /**
     * Draws lines from one location to the supplies locations
     *
     * @param from   Location to draw from
     * @param points Locations to draw to
     * @return       Output lines
     */
    public static List<Line> drawLinesFromCoordinate(Coordinate from, List<Coordinate> points)
    {
        List<Line> out = new ArrayList<>();
        if (points.size() > 0)
        {
            points.forEach(current -> out.add(LineFactory.getLine(from, current)));
        }
        return out;
    }

    /**
     * Draws lines from one location to the supplies locations
     *
     * @param from   Location to draw from
     * @param points Locations to draw to
     * @return       Output lines
     */
    public static List<Line> drawLinesFromNode(Node from, List<Node> points)
    {
        return drawLinesFromCoordinate(from.getCoord(), points.parallelStream().map(Node::getCoord).collect(Collectors.toList()));
    }
}
