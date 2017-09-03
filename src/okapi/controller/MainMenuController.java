package okapi.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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

            BiConsumer<Button, String> makeButton = (button, file) -> button.setOnAction(__ -> {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
                try {
                    mainTabPane.getSelectionModel().getSelectedItem().setContent(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            AnchorPane root = new AnchorPane();

            Button pidButton = new Button("PID Tuner");
            makeButton.accept(pidButton, "/view/pidTuner.fxml");

            Button autonButton = new Button("Autonomous Planner");
            makeButton.accept(autonButton, "/view/autonPlanner.fxml");

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(10, 0, 0, 10));
            hBox.setSpacing(10);
            hBox.getChildren().addAll(pidButton, autonButton);

            root.getChildren().add(hBox);

            Tab tab = new Tab("", root);
            Label label = new Label("Tab" + size);
            tab.setGraphic(label);
            TextField textField = new TextField();

            label.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2) {
                    textField.setText(label.getText());
                    tab.setGraphic(textField);
                    textField.selectAll();
                    textField.requestFocus();
                }
            });

            textField.setOnAction(action -> {
                label.setText(textField.getText());
                tab.setGraphic(label);
            });

            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    label.setText(textField.getText());
                    tab.setGraphic(label);
                }
            });

            mainTabPane.getTabs().add(size - 1, tab);
            mainTabPane.getSelectionModel().select(tab);
        });

        mainTabPane.getTabs().add(addTabTab);
    }
}
