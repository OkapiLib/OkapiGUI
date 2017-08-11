package okapi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class AutonPlannerField implements Initializable {
    @FXML
    private TilePane tilePane;

    private static final double ELEMENT_SIZE = 100;

    private int rows, cols;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rows = 3;
        cols = 3;

        tilePane.setStyle("-fx-background-color: rgba(255, 215, 0, 0.1);");
        tilePane.setHgap(0);
        tilePane.setVgap(0);

        setColumns(cols);
        setRows(rows);
    }

    public void setColumns(int newColumns) {
        cols = newColumns;
        tilePane.setPrefColumns(cols);
        createElements();
    }

    public void setRows(int newRows) {
        rows = newRows;
        tilePane.setPrefRows(rows);
        createElements();
    }

    private void createElements() {
        tilePane.getChildren().clear();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                tilePane.getChildren().add(createElement());
            }
        }
    }

    private Rectangle createElement() {
        Rectangle rectangle = new Rectangle(ELEMENT_SIZE, ELEMENT_SIZE);
        rectangle.setStroke(Color.ORANGE);
        rectangle.setFill(Color.STEELBLUE);
        return rectangle;
    }
}
