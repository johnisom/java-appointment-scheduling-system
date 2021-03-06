package controller;

import helper.dbaccess.dao.DBCountry;
import helper.dbaccess.dao.DBCustomer;
import helper.dbaccess.dao.DBDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The customer form controller that displays all the editable fields a blank, new customer or an already-filled-out, existing customer.
 * Validates all data given by user and refuses to save if data is not valid.
 */
public class CustomerFormController implements Initializable {
    /**
     * Represents the creation of a new customer or the editing of an existing customer.
     */
    private enum CustomerOperation { CREATE, EDIT }

    /**
     * The exception that is thrown if one or more fields are blank when trying to save the customer.
     */
    private static class FieldBlankException extends Exception {}

    public static final String viewFilename = "CustomerForm.fxml";
    public static final String desiredStageTitle = "Customer";
    public Stage currentStage;
    public MainController parentController;

    private Customer customer;
    private CustomerOperation currentCustomerOperation = CustomerOperation.CREATE;

    private final ObservableMap<String, Integer> divisionsNameToIdMap = FXCollections.observableHashMap();
    private final ObservableMap<String, Integer> countriesNameToIdMap = FXCollections.observableHashMap();

    public Label customerLabel;
    public TextField idTextField;
    public ChoiceBox<String> countryNameChoiceBox;
    public ChoiceBox<String> divisionNameChoiceBox;
    public TextField nameTextField;
    public TextField addressTextField;
    public TextField postalCodeTextField;
    public TextField phoneNumberTextField;

    /**
     * Initializes the CustomerFormController.
     * @param url the URL
     * @param resourceBundle the ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countriesNameToIdMap.clear();
        countryNameChoiceBox.getItems().clear();
        for (Country country : DBCountry.getAllCountries()) {
            countriesNameToIdMap.put(country.getName(), country.getId());
            countryNameChoiceBox.getItems().add(country.getName());
        }
    }

    /**
     * Invoked by the MainController when the "Add" button is clicked, and sets the fields &amp; labels accordingly.
     */
    public void addNewCustomer() {
        customerLabel.setText("New Customer");
    }

    /**
     * Invoked by the MainController when the "Modify" button is clicked, and sets the fields &amp; labels + currentCustomerOperation accordingly.
     * @param existingCustomer the customer that should be edited.
     */
    public void editExistingCustomer(Customer existingCustomer) {
        this.customer = existingCustomer;
        this.currentCustomerOperation = CustomerOperation.EDIT;

        customerLabel.setText("Customer " + customer.getId());
        idTextField.setText(String.valueOf(customer.getId()));
        nameTextField.setText(customer.getName());
        addressTextField.setText(customer.getAddress());
        postalCodeTextField.setText(customer.getPostalCode());
        phoneNumberTextField.setText(customer.getPhoneNumber());

        try {
            divisionNameChoiceBox.setValue(customer.getDivisionName());
            countryNameChoiceBox.setValue(customer.getDivision().getCountryName());
        } catch (Customer.DivisionNotFoundException | Division.CountryNotFoundException e) {
            e.printStackTrace();
        }
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
     * A generic helper method that shows a confirmation alert given the title and content, returning true if the user
     * confirmed the action, and false otherwise.
     *
     * @param title the text that is displayed on the alert window's title and in the header section
     * @param content the text that is displayed in the body of the alert
     * @return whether the user confirmed the action
     */
    private boolean showConfirmationAlert(String title, String content) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(title);
        confirmationAlert.setContentText(content);
        confirmationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Optional<ButtonType> userResponse = confirmationAlert.showAndWait();
        return userResponse.isPresent() && userResponse.get() == ButtonType.OK;
    }

    /**
     * Invoked when a country (U.K., Candaa, or U.S.) is selected from the countryNameChoiceBox.
     * It re-enables the divisionNameChoiceBox and populates it with the divisions for the selected country.
     */
    public void onCountrySelected() {
        String selectedCountryName = countryNameChoiceBox.getSelectionModel().getSelectedItem();
        Integer selectedCountryId = countriesNameToIdMap.get(selectedCountryName);

        divisionsNameToIdMap.clear();
        divisionNameChoiceBox.getItems().clear();

        if (selectedCountryName == null) {
            divisionNameChoiceBox.setDisable(true);
        } else {
            divisionNameChoiceBox.setDisable(false);
            for (Division division : DBDivision.getDivisionsWithCountryId(selectedCountryId)) {
                divisionsNameToIdMap.put(division.getName(), division.getId());
                divisionNameChoiceBox.getItems().add(division.getName());
            }
        }
    }

    /**
     * Invoked when the escape key is hit or the "Cancel" button is pressed, and closes the modal and doesn't make any changes if the action is confirmed by the user.
     * @see #showConfirmationAlert(String, String)
     */
    public void onCancel() {
        boolean shouldDiscardChanges = showConfirmationAlert("Discard Changes?", "Any changes made will be lost if you continue. Are you sure?");
        if (shouldDiscardChanges) {
            currentStage.close();
        }
    }

    /**
     * Invoked when the "Save" button is pressed, and closes the modal after saving the customer after validating the user input.
     * @see #checkFields() 
     * @see #updateCustomer() 
     * @see #createCustomer() 
     * @see MainController#updateCustomer(Customer) 
     * @see MainController#addCustomer(Customer) 
     * @see #showErrorAlert(String, String)
     */
    public void onSave() {
        try {
            checkFields();
            if (currentCustomerOperation == CustomerOperation.EDIT) {
                updateCustomer();
                parentController.updateCustomer(customer);
            } else if (currentCustomerOperation == CustomerOperation.CREATE) {
                createCustomer();
                parentController.addCustomer(customer);
            }

            currentStage.close();
        } catch (FieldBlankException e) {
            e.printStackTrace();
            showErrorAlert("Fields Blank!",
                    "One or more of the fields of the form are blank. Please fill them out and try again.");
        }
    }

    /**
     * Applies the fields to a new customer model and saves to the database.
     * <br>
     * The lambda allows us to use 1 line of code to assign the returned Optional&lt;Customer&gt; if it is present.
     * This saves 2 lines of code.
     * @see #applyFieldsToCustomer()
     * @see DBCustomer
     */
    private void createCustomer() {
        customer = new Customer();
        applyFieldsToCustomer();
        Optional<Customer> foundCustomer = DBCustomer.createCustomer(customer);
        foundCustomer.ifPresent(value -> customer = value);
    }

    /**
     * Applies the fields to the customer model and saves changes to the database.
     * <br>
     * The lambda allows us to use 1 line of code to assign the returned Optional&lt;Customer&gt; if it is present.
     * This saves 2 lines of code.
     * @see #applyFieldsToCustomer()
     * @see DBCustomer
     */
    private void updateCustomer() {
        applyFieldsToCustomer();
        DBCustomer.updateCustomer(customer);
        Optional<Customer> foundCustomer = DBCustomer.getCustomerFromId(customer.getId());
        foundCustomer.ifPresent(value -> customer = value);
    }

    /**
     * Takes all the user-input values in the form fields and sets the appropriate attributes in the customer model.
     * @see #customer
     */
    private void applyFieldsToCustomer() {
        customer.setDivisionId(divisionsNameToIdMap.get(divisionNameChoiceBox.getSelectionModel().getSelectedItem()));
        customer.setName(nameTextField.getText());
        customer.setAddress(addressTextField.getText());
        customer.setPostalCode(postalCodeTextField.getText());
        customer.setPhoneNumber(phoneNumberTextField.getText());
    }

    /**
     * Checks the user input and verifies that no fields are blank.
     * @throws FieldBlankException If any field is blank.
     */
    private void checkFields() throws FieldBlankException {
        String rawDivisionName = divisionNameChoiceBox.getSelectionModel().getSelectedItem();
        String rawName = nameTextField.getText();
        String rawAddress = addressTextField.getText();
        String rawPostalCode = postalCodeTextField.getText();
        String rawPhoneNumber = phoneNumberTextField.getText();
        if (rawDivisionName == null || rawDivisionName.isBlank() || rawName.isBlank() || rawAddress.isBlank() || rawPostalCode.isBlank() || rawPhoneNumber.isBlank()) {
            throw new FieldBlankException();
        }
    }
}
