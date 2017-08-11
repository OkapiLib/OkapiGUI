package okapi.jfx;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class NodeFactory {
    private static final int NODE_RADIUS = 8;
    private NodeListenerFactory listenerFactory;

    public NodeFactory(NodeListenerFactory listenerFactory) {
        this.listenerFactory = listenerFactory;
    }

    public Node getNode(double x, double y, double radius) {
        Node node = new Node(radius, x, y);
        listenerFactory.attachListeners(node);
        return node;
    }

    public Node getNode(double x, double y) {
        Node node = new Node(NODE_RADIUS, x, y);
        listenerFactory.attachListeners(node);
        return node;
    }
}
