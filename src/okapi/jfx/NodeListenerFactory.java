package okapi.jfx;

import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class NodeListenerFactory {
    private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY, mouseX, mouseY;
    private Node currentSelection;
    private Consumer<Node> deleteNode, redrawConnections;

    public NodeListenerFactory(Consumer<Node> deleteNode, Consumer<Node> redrawConnections) {
        this.deleteNode = deleteNode;
        this.redrawConnections = redrawConnections;
    }

    /**
     * Makes Nodes draggable and clickable with mouse listeners
     *
     * @param nodes Nodes to make draggable
     */
    public void attachListeners(Node... nodes) {
        Arrays.stream(nodes).forEach(node ->
                                     {
                                         node.setOnMousePressed(mousePressedHandler);
                                         node.setOnMouseDragged(mouseDraggedHandler);
                                         node.setOnContextMenuRequested(showContextMenu);
                                         node.setOnMouseMoved(trackMouseCoordinates);
                                     });
    }

    private EventHandler<ContextMenuEvent> showContextMenu = event -> {
//            ContextMenu contextMenu = new ContextMenu();
//
//            MenuItem changeName = new MenuItem("Change Name");
//            changeName.setOnAction(s -> editNodeName());
//
//            Menu changeCat = new Menu("Change Category");
//            List<Category> catsFromDB = Main.h.getAllCategories();
//            catsFromDB.forEach(s ->
//                               {
//                                   MenuItem item = new MenuItem(s.getCategory());
//                                   item.setOnAction(e -> {
//                                       currentSelection.getLocation().setCategory(s);
//                                       currentSelection.setHighlighted();
//                                       AdminMainController.updateNodePD();
//                                   });
//                                   changeCat.getItems().add(item);
//                               });
//
//            MenuItem remove = new MenuItem("Remove Node");
//            remove.setOnAction(event1 -> deleteCurrentSelection());
//
//            MenuItem autogen = new MenuItem("Generate Connections");
//            autogen.setOnAction(event1 -> {
//                //Generate neighbors for this node
//                List<Location> neighbors = NodeNeighbors.generateNeighbors(currentSelection, AdminMainController.displayedNodes);
//
//                currentSelection.getLocation().getNeighbors().clear();
//                for (Location neighbor : neighbors) {
//                    currentSelection.getLocation().addNeighbor(neighbor);
//                    neighbor.addNeighbor(currentSelection.getLocation());
//                    Main.h.addConnection(currentSelection.getLocation().getID(), neighbor.getID());
//                }
//
//                Main.h.setLocation(currentSelection.getLocation().getID(), currentSelection.getLocation());
//                AdminMainController.drawConnections(currentSelection);
//                AdminMainController.updateNodePD();
//            });
//
//            contextMenu.getItems().addAll(changeName, changeCat, autogen, remove);
//
//            contextMenu.show(currentSelection, mouseX, mouseY);
    };

    private EventHandler<MouseEvent> mousePressedHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.PRIMARY) {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();

                if (t.getSource() instanceof Node) {
                    Node p = ((Node) (t.getSource()));
                    orgTranslateX = p.getCenterX();
                    orgTranslateY = p.getCenterY();

                    if (t.isShiftDown() && p != currentSelection) {
                        //Add to neighbors
                        currentSelection.addNeighbor(p);
                        p.addNeighbor(currentSelection);
                    } else {
                        updateSelection(p);
                    }

                    redrawConnections.accept(currentSelection);
                } else {
                    Node p = ((Node) (t.getSource()));
                    orgTranslateX = p.getTranslateX();
                    orgTranslateY = p.getTranslateY();
                }
            } else if (t.getSource() instanceof Node) {
                updateSelection((Node) t.getSource());
            }
        }
    };

    private EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.PRIMARY) {
                double offsetX = t.getSceneX() - orgSceneX;
                double offsetY = t.getSceneY() - orgSceneY;

                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;

                if (t.getSource() instanceof Node) {
                    Node p = ((Node) (t.getSource()));
                    p.setCenter(newTranslateX, newTranslateY);

                    updateSelection(p);
                } else {
                    Node p = ((Node) (t.getSource()));
                    p.setTranslate(newTranslateX, newTranslateY);
                }
            }
        }
    };

    private EventHandler<MouseEvent> trackMouseCoordinates = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            mouseX = event.getScreenX();
            mouseY = event.getScreenY();
        }
    };

    /**
     * Updates the current selection; redraws highlight, connections, and node PD
     *
     * @param node New selection
     */
    public void updateSelection(Node node) {
        //Clear highlight
        if (currentSelection != null)
            currentSelection.setUnhighlighted();

        currentSelection = node;
        currentSelection.setHighlighted();
        redrawConnections.accept(currentSelection);
    }

    /**
     * Deletes the currently selected node
     */
    public void deleteCurrentSelection() {
        currentSelection.getNeighbors().forEach(elem -> elem.getNeighbors().remove(currentSelection));
        deleteNode.accept(currentSelection);
        redrawConnections.accept(null);
    }
}
