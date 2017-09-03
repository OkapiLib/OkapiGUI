package okapi.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class MainMenuController implements Initializable {
    @FXML
    private TabPane mainTabPane;

    private Stage stage;

    public MainMenuController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tab addTabTab = new Tab("+");
        addTabTab.setOnSelectionChanged(event -> {
            int size = mainTabPane.getTabs().size();

            AnchorPane root = new AnchorPane();

            BiConsumer<Button, String> makeButton = (button, file) -> {
                button.setOnAction(__ -> {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
                    try {
                        mainTabPane.getSelectionModel().getSelectedItem().setContent(loader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            };

            Button pidButton = new Button("PID Tuner");
            makeButton.accept(pidButton, "/view/pidTuner.fxml");

            Button autonButton = new Button("Autonomous Planner");
            makeButton.accept(autonButton, "/view/autonPlanner.fxml");

            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(pidButton, autonButton);

            root.getChildren().add(hBox);

            Tab newTab = new Tab("Tab " + size, root);
            mainTabPane.getTabs().add(size - 1, newTab);
            mainTabPane.getSelectionModel().select(newTab);
        });

        mainTabPane.getTabs().add(addTabTab);
    }
}
