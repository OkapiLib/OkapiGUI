package okapi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okapi.controller.MainMenuController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainMenuController controller = new MainMenuController(primaryStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setController(controller);
        loader.setLocation(getClass().getResource("/view/mainMenu.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("OkapiGUI");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
