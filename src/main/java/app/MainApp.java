package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println(
                getClass().getResource("/fxml/login.fxml")
        );

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load(), 500, 400);
        stage.setTitle("Copa do Mundo 2026");
        stage.setScene(scene);
        stage.show();
        System.out.println(getClass().getResource("/fxml/login.fxml"));
    }
}