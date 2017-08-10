package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class PidController implements Initializable {
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

    private List<Slider> sliders = new ArrayList<>();
    private List<TextField> textFields = new ArrayList<>();

    private static final int XAXIS_LENGTH = 100, XAXIS_DIV = 5;
    private int xAxisWindowLength = XAXIS_LENGTH / XAXIS_DIV;

    private Series<Number, Number> displayedSeries = new LineChart.Series<>();
    private ConcurrentHashMap<Integer, Integer> dataMap = new ConcurrentHashMap<>();
    private int tempCounter = 0;
    private int prevMax = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        displayedSeries.setName("Series 1");
        dataMap.put(0, 0);

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
            if (event.getButton().equals(MouseButton.SECONDARY))
            {
                ContextMenu menu = new ContextMenu();

                //Menu item to export the current data store to a csv
                MenuItem item = new MenuItem("Export current data to csv");
                item.setOnAction(event1 -> {
                    FileChooser fileChooser = new FileChooser();
                    File selection = fileChooser.showSaveDialog(lineChart.getScene().getWindow());
                    if (selection != null) {
                        StringBuilder csvBuilder = new StringBuilder();
                        for (LineChart.Data<Number, Number> elem : displayedSeries.getData())
                            csvBuilder.append(String.valueOf(elem.getXValue())).append(",").append(String.valueOf(elem.getYValue())).append("\n");

                        try (BufferedWriter writer = Files.newBufferedWriter(selection.toPath())) {
                            writer.write(csvBuilder.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                //Menu item to close the context menu
                MenuItem cancel = new MenuItem("Cancel");
                cancel.setOnAction(event1 -> menu.hide());

                menu.getItems().addAll(item, cancel);
                menu.show(lineChart, event.getScreenX(), event.getScreenY());
            }
        });

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
                } catch (NumberFormatException ignored) {}
            }));
        }

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    tempCounter += 1;
                    dataMap.put(tempCounter, (int)(127 * Math.abs(Math.sin(tempCounter / 100.0))));
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), actionEvent -> fillSeriesFromDataQueue()));
        SequentialTransition animation = new SequentialTransition();
        animation.getChildren().add(timeline);
        animation.play();
    }

    private void fillSeriesFromDataQueue() {
        if (!dataMap.isEmpty()) {
            //Pull most recent data off queue
            dataMap.keySet().stream().min(Integer::compareTo).ifPresent(val -> {
                //Only add new data if we have gone forward in time
                if (val > prevMax) {
                    prevMax = val;

                    displayedSeries.getData().add(new LineChart.Data<>(val, dataMap.get(val)));

                    //Trim the end of the plot
                    if (displayedSeries.getData().size() > xAxisWindowLength) {
                        xAxis.setLowerBound(xAxis.getLowerBound() + 1);
                        xAxis.setUpperBound(val);

                        //Keep the view as big as the user's preference
                        try {
                            if ((int)(val - xAxis.getLowerBound()) != Integer.valueOf(textFieldGraphViewWidth.getText()))
                                xAxis.setLowerBound(val - Double.valueOf(textFieldGraphViewWidth.getText()));
                        } catch (NumberFormatException ignored) {}
                    }

                    dataMap.remove(val);
                }
            });
        }
    }
}
