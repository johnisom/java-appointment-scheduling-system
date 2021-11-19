package controller;

import helper.dbaccess.dao.DBAppointment;
import helper.dbaccess.dao.DBContact;
import helper.dbaccess.dao.DBCustomer;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The main controller that displays reports, appointments, customers, opens modals for editing appointments &amp; customers,
 * and allows deleting of customers &amp; appointments.
 */
public class MainController implements Initializable {
    public static final String desiredStageTitle = "Main Stage";
    public static final String viewFilename = "Main.fxml";

    public Stage currentStage;
    public User loggedInUser;

    private final ObservableMap<String, Integer> contactsNameToIdMap = FXCollections.observableHashMap();
    
    public TableView<Customer> customersTableView;
    public TableColumn<Customer, Integer> customerIdTableColumn;
    public TableColumn<Customer, String> customerNameTableColumn;
    public TableColumn<Customer, String> customerAddressTableColumn;
    public TableColumn<Customer, String> customerPostalCodeTableColumn;
    public TableColumn<Customer, String> customerPhoneNumberTableColumn;
    public TableColumn<Customer, String> customerDivisionNameTableColumn;

    public TableView<Appointment> appointmentsTableView;
    public TableColumn<Appointment, Integer> appointmentIdTableColumn;
    public TableColumn<Appointment, String> appointmentTitleTableColumn;
    public TableColumn<Appointment, String> appointmentDescriptionTableColumn;
    public TableColumn<Appointment, String> appointmentLocationTableColumn;
    public TableColumn<Appointment, String> appointmentContactNameTableColumn;
    public TableColumn<Appointment, String> appointmentTypeTableColumn;
    public TableColumn<Appointment, String> appointmentStartsAtTableColumn;
    public TableColumn<Appointment, String> appointmentEndsAtTableColumn;
    public TableColumn<Appointment, Integer> customerIdColumn;
    public TableColumn<Appointment, Integer> userIdColumn;

    public ToggleGroup appointmentsTimeframeToggleGroup;
    public RadioButton appointmentsAllRadioButton;
    public RadioButton appointmentsWeeklyRadioButton;
    public RadioButton appointmentsMonthlyRadioButton;

    public TableView<List<StringProperty>> countByMonthAndTypeTableView;
    public TableColumn<List<StringProperty>, String> countByMonthAndTypeMonthTableColumn;
    public TableColumn<List<StringProperty>, String> countByMonthAndTypeTypeTableColumn;
    public TableColumn<List<StringProperty>, String> countByMonthAndTypeCountColumn;

    public TableView<List<StringProperty>> countByWeekdayAndTypeTableView;
    public TableColumn<List<StringProperty>, String> countByWeekdayAndTypeWeekdayTableColumn;
    public TableColumn<List<StringProperty>, String> countByWeekdayAndTypeTypeTableColumn;
    public TableColumn<List<StringProperty>, String> countByWeekdayAndTypeCountTableColumn;

    public ChoiceBox<String> contactNameChoiceBox;
    public TableView<Appointment> contactAppointmentsTableView;
    public TableColumn<Appointment, Integer> contactAppointmentIdTableColumn;
    public TableColumn<Appointment, String> contactAppointmentTitleTableColumn;
    public TableColumn<Appointment, String> contactAppointmentDescriptionTableColumn;
    public TableColumn<Appointment, String> contactAppointmentLocationTableColumn;
    public TableColumn<Appointment, String> contactAppointmentTypeTableColumn;
    public TableColumn<Appointment, String> contactAppointmentStartsAtTableColumn;
    public TableColumn<Appointment, String> contactAppointmentEndsAtTableColumn;
    public TableColumn<Appointment, Integer> contactCustomerIdColumn;
    public TableColumn<Appointment, Integer> contactUserIdColumn;

    /**
     * Initializes the MainController.
     * <br>
     * Description of Lambdas:
     * <dl>
     *     <dt>Lambda 1</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByMonthAndTypeMonthTableColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     *     <dt>Lambda 2</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByMonthAndTypeTypeTableColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     *     <dt>Lambda 3</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByMonthAndTypeCountColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     *     <dt>Lambda 4</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByWeekdayAndTypeWeekdayTableColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     *     <dt>Lambda 5</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByWeekdayAndTypeTypeTableColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     *     <dt>Lambda 6</dt>
     *     <dd>
     *         This lambda sets the cell value factory for the countByWeekdayAndTypeCountTableColumn TableColumn.
     *         This is necessary because the data used is just a 2d matrix (nested list) of strings,
     *         not an object that has properties.
     *     </dd>
     * </dl>
     * @param url the URL
     * @param resourceBundle the ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactsNameToIdMap.clear();
        contactNameChoiceBox.getItems().clear();
        for (Contact contact : DBContact.getAllContacts()) {
            contactsNameToIdMap.put(contact.getName(), contact.getId());
            contactNameChoiceBox.getItems().add(contact.getName());
        }

        customerIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCodeTableColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneNumberTableColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerDivisionNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        appointmentIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationTableColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartsAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("formattedStartsAt"));
        appointmentEndsAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("formattedEndsAt"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        countByMonthAndTypeMonthTableColumn.setCellValueFactory(data -> data.getValue().get(0)); // Lambda 1
        countByMonthAndTypeTypeTableColumn.setCellValueFactory(data -> data.getValue().get(1)); // Lambda 2
        countByMonthAndTypeCountColumn.setCellValueFactory(data -> data.getValue().get(2)); // Lambda 3

        countByWeekdayAndTypeWeekdayTableColumn.setCellValueFactory(data -> data.getValue().get(0)); // Lambda 4
        countByWeekdayAndTypeTypeTableColumn.setCellValueFactory(data -> data.getValue().get(1)); // Lambda 5
        countByWeekdayAndTypeCountTableColumn.setCellValueFactory(data -> data.getValue().get(2)); // Lambda 6

        contactAppointmentIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        contactAppointmentTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        contactAppointmentDescriptionTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactAppointmentLocationTableColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactAppointmentTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactAppointmentStartsAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("formattedStartsAt"));
        contactAppointmentEndsAtTableColumn.setCellValueFactory(new PropertyValueFactory<>("formattedEndsAt"));
        contactCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        contactUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        populateCustomersTableView();
        populateAppointmentsTableView();
        populateReports();
    }

    /**
     * Populates the table view for the Customer model with all customers.
     * Pulls from DB.
     */
    private void populateCustomersTableView() {
        customersTableView.setItems(DBCustomer.getAllCustomers());
    }

    /**
     * Populates the table view for the Appointment model with all appointments.
     * Pulls from DB.
     */
    private void populateAppointmentsTableView() {
        if (appointmentsMonthlyRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointmentsStartingWithinNextMonth());
        } else if (appointmentsWeeklyRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointmentsStartingWithinNextWeek());
        } else if (appointmentsAllRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointments());
        }
    }

    /**
     * Populates data output for each of the 3 individual reports.
     */
    private void populateReports() {
        populateMonthAndTypeReport();
        populateWeekdayAndTypeReport();
        populateContactsSchedulesReport();
    }

    /**
     * Populates the report for month and type count.
     */
    private void populateMonthAndTypeReport() {
        countByMonthAndTypeTableView.setItems(DBAppointment.getAppointmentsCountByMonthAndType());
    }

    /**
     * Populates the report for weekday and type count.
     */
    private void populateWeekdayAndTypeReport() {
        countByWeekdayAndTypeTableView.setItems(DBAppointment.getAppointmentsCountByWeekdayAndType());
    }

    /**
     * Populates the report for each contact's schedule.
     * If a contact has yet to be selected, then it populates nothing.
     */
    private void populateContactsSchedulesReport() {
        if (contactNameChoiceBox.getValue() != null) {
            Integer contactId = contactsNameToIdMap.get(contactNameChoiceBox.getValue());
            if (contactId != null) {
                contactAppointmentsTableView.setItems(DBAppointment.getAllAppointmentsForContactId(contactId));
            }
        }
    }

    /**
     * Grabs the selected Customer model from the customersTableView, or null if no customer selected.
     * @return the selected customer.
     */
    private Customer getSelectedCustomer() {
        return customersTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Grabs the selected Appointment model from the appointmentsTableView, or null if no appointment selected.
     * @return the selected appointment.
     */
    private Appointment getSelectedAppointment() {
        return appointmentsTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * When a user clicks the "Add" or "Modify" buttons in the customers tab, it opens a new modal window.
     * @return The controller for the newly-created Stage/Scene combo.
     * @throws IOException If the view file cannot be found.
     */
    private CustomerFormController openCustomerModal() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + CustomerFormController.viewFilename));
        Parent root = loader.load();
        CustomerFormController controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle(CustomerFormController.desiredStageTitle);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        controller.currentStage = stage;
        controller.parentController = this;

        return controller;
    }

    /**
     * When a user clicks the "Add" or "Modify" buttons in the appointments tab, it opens a new modal window.
     * @return The controller for the newly-created Stage/Scene combo.
     * @throws IOException If the view file cannot be found.
     */
    private AppointmentFormController openAppointmentModal() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + AppointmentFormController.viewFilename));
        Parent root = loader.load();
        AppointmentFormController controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle(AppointmentFormController.desiredStageTitle);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        controller.currentStage = stage;
        controller.parentController = this;

        return controller;
    }

    /**
     * A generic helper method that shows an error alert given the title and content.
     *
     * @param title the text that is displayed on the alert window's title and in the header section.
     * @param content the text that is displayed in the body of the alert.
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
     * @param title the text that is displayed on the alert window's title and in the header section.
     * @param content the text that is displayed in the body of the alert.
     * @return whether the user confirmed the action.
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

    /**.
     * A generic helper method that shows an info alert given the title and content.
     *
     * @param title the text that is displayed on the alert window's title and in the header section.
     * @param content the text that is displayed in the body of the alert.
     */
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

    /**
     * Upon log-in, shows info alert for either:
     * <ul>
     * <li>All appointments for the user within the next 15 mins; or</li>
     * <li>A message displaying that there are no upcoming appointments.</li>
     * </ul>
     */
    public void showAppointmentsWithinNext15Mins() {
        ObservableList<Appointment> upcomingAppointments = DBAppointment.getAppointmentsStartingWithinNext15MinsForUserId(loggedInUser.getId());
        String upcomingAppointmentsText;
        if (upcomingAppointments.size() == 0) {
            upcomingAppointmentsText = "There are no upcoming appointments.";
        } else {
            upcomingAppointmentsText = String.format("You have upcoming appointments:%n%s.", upcomingAppointments.stream().map(Appointment::toPrettyString).collect(Collectors.joining(",\n")));
        }

        showInfoAlert("Upcoming Appointments", upcomingAppointmentsText);
    }

    /**
     * Called by the CustomerController when a customer has been created. Adds the customer to the customersTableView.
     * @param newCustomer the customer that was just created.
     */
    public void addCustomer(Customer newCustomer) {
        customersTableView.getItems().add(newCustomer);
    }

    /**
     * Called by the CustomerController when a customer has been updated. Updates the customer in the customersTableView.
     * <br>
     * The lambda, given  the customer that was just updated,
     * replaces that customer in customersTableView TableView by comparing ids.
     * The alternate approach would be to find the index of that customer, delete the element at that index,
     * and then add the customer, but this lambda allows us to do the replacement in one succinct step.
     * @param customer the customer that was just udpated.
     */
    public void updateCustomer(Customer customer) {
        customersTableView.getItems().replaceAll(cust -> cust.getId() == customer.getId() ? customer : cust); // TODO: document this lambda
        populateReports();
    }

    /**
     * Called by the AppointmentController when an appointment has been created. Refreshes the appointmentsTableView and all reports.
     */
    public void addAppointment() {
        populateAppointmentsTableView();
        populateReports();
    }


    /**
     * Called by the AppointmentController when an appointment has been updated. Refreshes the appointmentsTableView and all reports.
     */
    public void updateAppointment() {
        populateAppointmentsTableView();
        populateReports();
    }

    /**
     * WInvoked when the user hits escape or clicks the "Quit" button, and closes the currentStage.
     */
    public void onQuit() {
        currentStage.close();
    }

    /**
     * Invoked when the "Add" button is clicked in the customers tab, and opens the customer modal.
     * @throws IOException if the view file cannot be found.
     * @see #openCustomerModal()
     */
    public void onCustomerAdd() throws IOException {
        CustomerFormController controller = openCustomerModal();
        controller.addNewCustomer();
    }

    /**
     * Invoked when the "Modify" button is clicked in the customers tab, and opens the customer modal.
     * @throws IOException if the view file cannot be found.
     * @see #openCustomerModal()
     */
    public void onCustomerModify() throws IOException {
        Customer customer = getSelectedCustomer();
        if (customer != null) {
            CustomerFormController controller = openCustomerModal();
            controller.editExistingCustomer(customer);
        }
    }

    /**
     * Invoked when the "Delete" button is clicked in the customers tab, and deletes the selected customer.
     * Only deletes if the user confirms the action.
     */
    public void onCustomerDelete() {
        Customer customer = getSelectedCustomer();
        if (customer != null) {
            boolean shouldDeleteCustomer = showConfirmationAlert("Delete Customer?",
                    "Are you sure you want to permanently delete the customer (having name \"" +
                            customer.getName() + "\" and id " + customer.getId() + ")?");
            if (shouldDeleteCustomer) {
                if (DBCustomer.deleteCustomerFromId(customer.getId())) {
                    customersTableView.getItems().remove(customer);
                    populateAppointmentsTableView();
                    populateReports();
                } else {
                    showErrorAlert("Delete Failed!", "The customer could not be deleted!");
                }
            }
        }
    }

    /**
     * Invoked when the "Add" button is clicked in the appointments tab, and opens the appointment modal.
     * @throws IOException if the view file cannot be found.
     * @see #openAppointmentModal()
     */
    public void onAppointmentAdd() throws IOException {
        AppointmentFormController controller = openAppointmentModal();
        controller.addNewAppointment();
    }

    /**
     * Invoked when the "Modify" button is clicked in the appointments tab, and opens the appointment modal.
     * @throws IOException if the view file cannot be found.
     * @see #openAppointmentModal()
     */
    public void onAppointmentModify() throws IOException {
        Appointment appointment = getSelectedAppointment();
        if (appointment != null) {
            AppointmentFormController controller = openAppointmentModal();
            controller.editExistingAppointment(appointment);
        }
    }

    /**
     * Invoked when the "Delete" button is clicked in the appointments tab, and deletes the selected appointment.
     * Only deletes if the user confirms the action.
     */
    public void onAppointmentDelete() {
        Appointment appointment = getSelectedAppointment();
        if (appointment != null) {
            boolean shouldDeleteAppointment = showConfirmationAlert("Delete Appointment?",
                    "Are you sure you want to permanently delete the appointment (having title \"" +
                            appointment.getTitle() + "\" and id " + appointment.getId() + ")?");
            if (shouldDeleteAppointment) {
                if (DBAppointment.deleteAppointmentFromId(appointment.getId())) {
                    appointmentsTableView.getItems().remove(appointment);
                    populateReports();
                } else {
                    showErrorAlert("Delete Failed!", "The appointment could not be deleted!");
                }
            }
        }
    }

    /**
     * Invoked when one of the "Monthly"/"Weekly"/"All" buttons are clicked in the appointments tab, and repopulates the appointmentsTableView.
     */
    public void onAppointmentsTimeframeToggle() {
        populateAppointmentsTableView();
    }

    /**
     * Invoked when the contact choice box is selected in the reports tab, and repopulates the contactAppointmentsTableView.
     */
    public void onContactNameSelected() {
        populateContactsSchedulesReport();
    }
}
