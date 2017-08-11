package okapi.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import okapi.jfx.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Ryan Benasutti
 * @since 2017-08-11
 */
public class AutonPlanner implements Initializable {
    private ObservableList<Node> displayedNodes = FXCollections.observableArrayList();
    private ObservableList<Line> displayedLines = FXCollections.observableArrayList();
    private Pane nodeOverlay, lineOverlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nodeOverlay = new Pane();
        nodeOverlay.setPickOnBounds(false);
        lineOverlay = new Pane();
        lineOverlay.setPickOnBounds(false);

        displayedNodes.addListener((ListChangeListener<? super Node>) c -> nodeOverlay.getChildren().setAll(displayedNodes));

        nodeOverlay.getChildren().setAll(displayedNodes);

        Group displayGroup = new Group(lineOverlay, nodeOverlay);
        ScrollPane pane = new ScrollPane(new Pane(displayGroup));
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setPannable(true);
        displayGroup.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getButton() != MouseButton.MIDDLE &&
                !(event.getButton() == MouseButton.PRIMARY && event.isControlDown()))
                event.consume();
        });

        NodeListenerFactory listenerFactory = new NodeListenerFactory((node -> displayedNodes.remove(node)),
                                                                      (node -> {
                                                                          displayedLines = FXCollections.observableArrayList(NodeRenderer.drawLinesFromNode(node, node.getNeighbors()));
                                                                          lineOverlay.getChildren().setAll(displayedLines);
                                                                      }));
        NodeFactory nodeFactory = new NodeFactory(listenerFactory);
    }
}
