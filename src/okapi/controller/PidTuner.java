package okapi.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import okapi.jinx.JinxSource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PidTuner implements Initializable {
    @FXML
    private Slider sliderKP;
    @FXML
    private TextField textFieldKP;
    @FXML
    private Slider sliderKI;
    @FXML
    private TextField textFieldKI;
    @FXML
    private Slider sliderKD;
    @FXML
    private TextField textFieldKD;
    @FXML
    private Slider sliderIntegralMin;
    @FXML
    private TextField textFieldIntegralMin;
    @FXML
    private Slider sliderIntegralMax;
    @FXML
    private TextField textFieldIntegralMax;
    @FXML
    private Slider sliderOutputMin;
    @FXML
    private TextField textFieldOutputMin;
    @FXML
    private Slider sliderOutputMax;
    @FXML
    private TextField textFieldOutputMax;

    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis, yAxis;

    @FXML
    private TextField textFieldGraphViewWidth;

    private static final int XAXIS_LENGTH = 100, XAXIS_DIV = 5;
    private int xAxisWindowLength = XAXIS_LENGTH / XAXIS_DIV;

    private Series<Number, Number> displayedSeries = new LineChart.Series<>();
    private JinxSource source = new JinxSource();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSliderAndTextFieldListeners();

        displayedSeries.setName("Series 1");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(XAXIS_LENGTH);
        xAxis.setTickUnit(XAXIS_DIV);
        xAxis.setOnScroll(event -> {
            xAxis.setLowerBound(xAxis.getLowerBound() + event.getDeltaY());
            textFieldGraphViewWidth.setText(String.valueOf(xAxis.getUpperBound() - xAxis.getLowerBound()));
        });

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(127);
        yAxis.setTickUnit(10);

        lineChart.getData().add(displayedSeries);
        lineChart.setCreateSymbols(false);
        lineChart.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                ContextMenu menu = new ContextMenu();

                //Menu item to export the current data store to a csv
                MenuItem item = new MenuItem("Export current data to csv");
                item.setOnAction(__ -> {
                    FileChooser fileChooser = new FileChooser();
                    File selection = fileChooser.showSaveDialog(lineChart.getScene().getWindow());
                    if (selection != null)
                        source.exportDataMapToCSV(selection);
                });

                //Menu item to close the context menu
                MenuItem cancel = new MenuItem("Cancel");
                cancel.setOnAction(__ -> menu.hide());

                menu.getItems().addAll(item, cancel);
                menu.show(lineChart, event.getScreenX(), event.getScreenY());
            }
        });

        source.setCallback((key, val) -> Platform.runLater(() -> addToSeries(key, val)));
        source.open();
    }

    private void setSliderAndTextFieldListeners() {
        List<Slider> sliders = new ArrayList<>();
        List<TextField> textFields = new ArrayList<>();

        sliders.add(sliderKP);
        sliders.add(sliderKI);
        sliders.add(sliderKD);
        sliders.add(sliderIntegralMin);
        sliders.add(sliderIntegralMax);
        sliders.add(sliderOutputMin);
        sliders.add(sliderOutputMax);
        textFields.add(textFieldKP);
        textFields.add(textFieldKI);
        textFields.add(textFieldKD);
        textFields.add(textFieldIntegralMin);
        textFields.add(textFieldIntegralMax);
        textFields.add(textFieldOutputMin);
        textFields.add(textFieldOutputMax);

        //Set listeners
        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            TextField field = textFields.get(i);
            slider.setMax(127);
            slider.setMin(0);
            slider.valueProperty().addListener(((observable, oldValue, newValue) -> {
                field.setText(String.valueOf(newValue));
            }));
            field.textProperty().addListener(((observable, oldValue, newValue) -> {
                try {
                    slider.setValue(Double.valueOf(newValue));
                } catch (NumberFormatException ignored) {
                }
            }));
        }
    }

    private void addToSeries(Integer key, Integer value) {
        displayedSeries.getData().add(new LineChart.Data<>(key, value));

        //Trim the end of the plot
        if (displayedSeries.getData().size() > xAxisWindowLength) {
            xAxis.setLowerBound(xAxis.getLowerBound() + 1);
            xAxis.setUpperBound(key);

            //Keep the view as big as the user's preference
            try {
                if ((int) (key - xAxis.getLowerBound()) != Integer.valueOf(textFieldGraphViewWidth.getText())) {
                    xAxis.setLowerBound(key - Double.valueOf(textFieldGraphViewWidth.getText()));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
