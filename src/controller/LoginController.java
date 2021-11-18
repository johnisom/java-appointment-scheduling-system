package controller;

import helper.dbaccess.dao.DBUser;
import helper.locale.LocaleHelper;
import helper.loginactivity.LoginActivityLogger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public static final String desiredStageTitle = LocaleHelper.getTranslation("loginStageTitle");
    public static final String viewFilename = "Login.fxml";

    public Stage currentStage;

    public Label loginGreetingLabel1;
    public Label loginGreetingLabel2;
    public Label locationLabel;
    public TextField usernameTextField;
    public PasswordField passwordTextField;
    public Button submitButton;
    public Button quitButton;

    private User loggedInUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginGreetingLabel1.setText(LocaleHelper.getTranslation("loginGreeting1"));
        loginGreetingLabel2.setText(LocaleHelper.getTranslation("loginGreeting2"));
        locationLabel.setText(LocaleHelper.getTranslation("yourLocationLabel") + " " + LocaleHelper.getLocation());
        usernameTextField.setPromptText(LocaleHelper.getTranslation("usernameFieldPrompt"));
        passwordTextField.setPromptText(LocaleHelper.getTranslation("passwordFieldPrompt"));
        submitButton.setText(LocaleHelper.getTranslation("submitButtonPrompt"));
        quitButton.setText(LocaleHelper.getTranslation("quitButtonPrompt"));
    }

    private void showMainPage() throws IOException {
        Stage mainStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/" + MainController.viewFilename)));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.currentStage = mainStage;
        controller.loggedInUser = loggedInUser;
        Scene scene = new Scene(root);
        mainStage.setTitle(MainController.desiredStageTitle);
        mainStage.setScene(scene);
        mainStage.show();
        controller.showAppointmentsWithinNext15Mins();
        currentStage.close();
    }

    private void showInvalidLoginMessage() {
        showErrorAlert(LocaleHelper.getTranslation("invalidLoginAlertTitle"), LocaleHelper.getTranslation("invalidLoginAlertContent"));
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    public void onQuit() {
        currentStage.close();
    }

    public void onSubmit() throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        Optional<User> optionalUser = DBUser.getUserFromUsernameAndPassword(username, password);
        if (optionalUser.isPresent()) {
            LoginActivityLogger.logAttempt(username, true);
            loggedInUser = optionalUser.get();
            showMainPage();
        } else {
            LoginActivityLogger.logAttempt(username, false);
            showInvalidLoginMessage();
            usernameTextField.clear();
            passwordTextField.clear();
            usernameTextField.requestFocus();
        }
    }
}
