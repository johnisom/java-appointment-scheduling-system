package main;

import controller.LoginController;
import helper.dbaccess.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        DBConnection.openConnection();
        launch(args);
        DBConnection.closeConnection();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/" + LoginController.viewFilename)));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.currentStage = primaryStage;
        Scene scene = new Scene(root);
        primaryStage.setTitle(LoginController.desiredStageTitle);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
