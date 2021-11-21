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

/**
 * The login controller that displays the log-in form and displays the main page if the correct credentials are entered.
 * This form is translated to either French or English, depending on the user's computer locale.
 */
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

    /**
     * Initializes the LoginController.
     * @param url the URL
     * @param resourceBundle the ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginGreetingLabel1.setText(LocaleHelper.getTranslation("loginGreeting1"));
        loginGreetingLabel2.setText(LocaleHelper.getTranslation("loginGreeting2"));
        locationLabel.setText(LocaleHelper.getTranslation("yourLocationLabel") + "\n" + LocaleHelper.getLocation());
        usernameTextField.setPromptText(LocaleHelper.getTranslation("usernameFieldPrompt"));
        passwordTextField.setPromptText(LocaleHelper.getTranslation("passwordFieldPrompt"));
        submitButton.setText(LocaleHelper.getTranslation("submitButtonPrompt"));
        quitButton.setText(LocaleHelper.getTranslation("quitButtonPrompt"));
    }

    /**
     * When a user successfully logs in, initializes the main view + controller and closes the currentStage.
     * @throws IOException if the view file cannot be found.
     */
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

    /**
     * Shows an error alert that alerts the user that the login credentials they entered are invalid.
     * @see #showErrorAlert(String, String) 
     */
    private void showInvalidLoginMessage() {
        showErrorAlert(LocaleHelper.getTranslation("invalidLoginAlertTitle"), LocaleHelper.getTranslation("invalidLoginAlertContent"));
    }

    /**
     * A generic helper method that shows an error alert given the title and content.
     *
     * @param title the text that is displayed on the alert window's title and in the header section
     * @param content the text that is displayed in the body of the alert
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    /**
     * Invoked when the "Quit" button or escape button is pressed.
     */
    public void onQuit() {
        currentStage.close();
    }

    /**
     * Invoked when the "Submit" button in the log-in form is clicked, and authenticates the user.
     * Displays main page if login credentials are correct.
     * @throws IOException if the view file cannot be found.
     * @see #showMainPage()
     */
    public void onSubmit() throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        Optional<User> optionalUser = DBUser.getUserFromUsernameAndPassword(username, password);
        if (optionalUser.isPresent()) {
            try {
                LoginActivityLogger.logAttempt(username, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            loggedInUser = optionalUser.get();
            showMainPage();
        } else {
            try {
                LoginActivityLogger.logAttempt(username, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            showInvalidLoginMessage();
            usernameTextField.clear();
            passwordTextField.clear();
            usernameTextField.requestFocus();
        }
    }
}
