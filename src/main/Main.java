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

/**
 * The main class that invokes the main function. The entry point of the application.
 * @see #main(String[])
 */
public class Main extends Application {

    /**
     * Runs the JavaFX application by opening a connection to the database, launching the JavaFX application,
     * then closing the connection to the database when the JavaFX application has ended.
     * @param args command line arguments.
     */
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
