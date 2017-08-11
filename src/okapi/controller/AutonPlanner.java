package okapi.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import okapi.jfx.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AutonPlanner implements Initializable {
    private ObservableList<Node> displayedNodes = FXCollections.observableArrayList();
    private ObservableList<Line> displayedLines = FXCollections.observableArrayList();
    private Pane nodeOverlay, lineOverlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayedNodes.addListener((ListChangeListener<? super Node>) c -> nodeOverlay.getChildren().setAll(displayedNodes));

        NodeListenerFactory listenerFactory = new NodeListenerFactory((node -> displayedNodes.remove(node)),
                                                                      (node -> {
                                                                          displayedLines = FXCollections.observableArrayList(NodeRenderer.drawLinesFromNode(node, node.getNeighbors()));
                                                                          lineOverlay.getChildren().setAll(displayedLines);
                                                                      }));
        NodeFactory nodeFactory = new NodeFactory(listenerFactory);
    }
}
