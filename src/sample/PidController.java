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
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class PidController implements Initializable {
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis, yAxis;
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

    private List<Slider> sliders = new ArrayList<>();
    private List<TextField> textFields = new ArrayList<>();

    private static final int XAXIS_LENGTH = 100, XAXIS_DIV = 5;
    private static final int XAXIS_COUNT = (int)(XAXIS_LENGTH / (float)XAXIS_DIV);

    private Series<Number, Number> displayedSeries = new LineChart.Series<>();
    private ConcurrentHashMap<Integer, Integer> dataMap = new ConcurrentHashMap<>();
    private int tempCounter = 0;
    private int prevMax = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayedSeries.setName("Series 1");
        dataMap.put(0, 0);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(XAXIS_LENGTH);
        xAxis.setTickUnit(XAXIS_DIV);
        xAxis.setOnScroll(event -> xAxis.setLowerBound(xAxis.getLowerBound() + event.getDeltaY()));

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(127);
        yAxis.setTickUnit(10);

        lineChart.getData().add(displayedSeries);
        lineChart.setCreateSymbols(false);

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

        for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);
            TextField field = textFields.get(i);
            slider.setMax(127);
            slider.setMin(0);
            slider.valueProperty().addListener(((observable, oldValue, newValue) -> field.setText(String.valueOf(newValue))));
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
                    if (displayedSeries.getData().size() > XAXIS_COUNT) {
                        xAxis.setUpperBound(val);
                    }

                    dataMap.remove(val);
                }
            });
        }
    }
}