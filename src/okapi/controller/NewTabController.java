package okapi.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class NewTabController implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Button pidTuner, autonPlanner;

    private final BiConsumer<Button, String> replaceRoot = ((button, s) -> button.setOnAction(event -> {
        try {
            pane.getChildren().setAll((Node) FXMLLoader.load(this.getClass().getResource(s)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        replaceRoot.accept(pidTuner, "/view/pidTuner.fxml");
        replaceRoot.accept(autonPlanner, "/view/autonPlanner.fxml");
    }
}
