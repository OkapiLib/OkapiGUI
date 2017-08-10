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
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Initializable {
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis, yAxis;

    private static final int XAXIS_LENGTH = 100, XAXIS_DIV = 5;
    private static final int XAXIS_COUNT = (int)(XAXIS_LENGTH / (float)XAXIS_DIV);

    private Series<Number, Number> displayedSeries;
    private ConcurrentHashMap<Integer, Integer> dataMap;
    private int tempCounter = 0;
    private int prevMax = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayedSeries = new LineChart.Series<>();
        dataMap = new ConcurrentHashMap<>();
        dataMap.put(0, 0);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(XAXIS_LENGTH);
        xAxis.setTickUnit(XAXIS_DIV);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(127);
        yAxis.setTickUnit(10);

        lineChart.getData().add(displayedSeries);

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    tempCounter += 1;
                    dataMap.put(tempCounter, (int)(127 * Math.abs(Math.sin(tempCounter / 0.00001))));
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
//                        displayedSeries.getData().remove(0, 1);
                        xAxis.setLowerBound(0);
                        xAxis.setUpperBound(val);
                    }

                    dataMap.remove(val);
                }
            });
        }
    }
}
