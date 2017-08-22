package okapi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    @FXML
    private TabPane mainTabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tab addTabTab = new Tab("+");
        addTabTab.setOnSelectionChanged(event -> {
            int size = mainTabPane.getTabs().size();
            Tab newTab = new Tab("Tab " + size);
            mainTabPane.getTabs().add(size - 1, newTab);
            mainTabPane.getSelectionModel().select(newTab);
        });

        mainTabPane.getTabs().add(addTabTab);
    }
}
