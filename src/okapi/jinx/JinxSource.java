package okapi.jinx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class JinxSource {
    private ConcurrentHashMap<Integer, Integer> dataMap;
    private BiConsumer<Integer, Integer> callback;
    private Thread comms;

    private int tempCounter = 0;

    public JinxSource() {
        dataMap = new ConcurrentHashMap<>();
    }

    /**
     * Open the JINX thread and begin comms
     */
    public void open() {
        //simulate
        comms = new Thread(() -> {
            while (true) {
                try {
                    tempCounter += 1;

                    int key = tempCounter;
                    int value = (int) (127 * Math.abs(Math.sin(tempCounter / 100.0)));
                    dataMap.put(key, value);

                    if (callback != null)
                        callback.accept(key, value);

                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        comms.start();
    }

    /**
     * Close the JINX thread and end comms
     */
    public void close() {
        try {
            comms.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exports the internal data to a CSV file
     * @param file File to export to
     */
    public void exportDataMapToCSV(File file) {
        StringBuilder csvBuilder = new StringBuilder();

        for (Integer key : dataMap.keySet())
            csvBuilder.append(String.valueOf(key)).append(",").append(String.valueOf(dataMap.get(key))).append("\n");

        //Append .csv if it's not there already
        String path = file.toPath().toString();
        if (!path.endsWith(".csv"))
            path += ".csv";

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            writer.write(csvBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the new data callback
     * @param callback Callback accepting a new data element
     */
    public void setCallback(BiConsumer<Integer, Integer> callback) {
        this.callback = callback;
    }

    public ConcurrentHashMap<Integer, Integer> getDataMap() {
        return dataMap;
    }
}
