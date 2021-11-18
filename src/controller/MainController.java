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
 * The main controller that displays reports, appointments, customers, opens modals for editing appointments & customers,
 * and allows deleting of customers & appointments.
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

        countByMonthAndTypeMonthTableColumn.setCellValueFactory(data -> data.getValue().get(0)); // TODO: document this lambda
        countByMonthAndTypeTypeTableColumn.setCellValueFactory(data -> data.getValue().get(1)); // TODO: document this lambda
        countByMonthAndTypeCountColumn.setCellValueFactory(data -> data.getValue().get(2)); // TODO: document this lambda

        countByWeekdayAndTypeWeekdayTableColumn.setCellValueFactory(data -> data.getValue().get(0)); // TODO: document this lambda
        countByWeekdayAndTypeTypeTableColumn.setCellValueFactory(data -> data.getValue().get(1)); // TODO: document this lambda
        countByWeekdayAndTypeCountTableColumn.setCellValueFactory(data -> data.getValue().get(2)); // TODO: document this lambda

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
     *
     */
    private void populateCustomersTableView() {
        customersTableView.setItems(DBCustomer.getAllCustomers());
    }

    private void populateAppointmentsTableView() {
        if (appointmentsMonthlyRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointmentsStartingWithinNextMonth());
        } else if (appointmentsWeeklyRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointmentsStartingWithinNextWeek());
        } else if (appointmentsAllRadioButton.isSelected()) {
            appointmentsTableView.setItems(DBAppointment.getAllAppointments());
        }
    }

    private void populateReports() {
        populateMonthAndTypeReport();
        populateWeekdayAndTypeReport();
        populateContactsSchedulesReport();
    }

    private void populateMonthAndTypeReport() {
        countByMonthAndTypeTableView.setItems(DBAppointment.getAppointmentsCountByMonthAndType());
    }

    private void populateWeekdayAndTypeReport() {
        countByWeekdayAndTypeTableView.setItems(DBAppointment.getAppointmentsCountByWeekdayAndType());
    }

    private void populateContactsSchedulesReport() {
        if (contactNameChoiceBox.getValue() != null) {
            Integer contactId = contactsNameToIdMap.get(contactNameChoiceBox.getValue());
            if (contactId != null) {
                contactAppointmentsTableView.setItems(DBAppointment.getAllAppointmentsForContactId(contactId));
            }
        }
    }

    private Customer getSelectedCustomer() {
        return customersTableView.getSelectionModel().getSelectedItem();
    }

    private Appointment getSelectedAppointment() {
        return appointmentsTableView.getSelectionModel().getSelectedItem();
    }

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

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.show();
    }

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

    public void addCustomer(Customer newCustomer) {
        customersTableView.getItems().add(newCustomer);
    }

    public void updateCustomer(Customer customer) {
        customersTableView.getItems().replaceAll(cust -> cust.getId() == customer.getId() ? customer : cust); // TODO: document this lambda
        populateReports();
    }

    public void addAppointment() {
        populateAppointmentsTableView();
        populateReports();
    }

    public void updateAppointment() {
        populateAppointmentsTableView();
        populateReports();
    }

    public void onQuit() {
        currentStage.close();
    }

    public void onCustomerAdd() throws IOException {
        CustomerFormController controller = openCustomerModal();
        controller.addNewCustomer();
    }

    public void onCustomerModify() throws IOException {
        Customer customer = getSelectedCustomer();
        if (customer != null) {
            CustomerFormController controller = openCustomerModal();
            controller.editExistingCustomer(customer);
        }
    }

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

    public void onAppointmentAdd() throws IOException {
        AppointmentFormController controller = openAppointmentModal();
        controller.addNewAppointment();
    }

    public void onAppointmentModify() throws IOException {
        Appointment appointment = getSelectedAppointment();
        if (appointment != null) {
            AppointmentFormController controller = openAppointmentModal();
            controller.editExistingAppointment(appointment);
        }
    }

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

    public void onAppointmentsTimeframeToggle() {
        populateAppointmentsTableView();
    }

    public void onContactNameSelected() {
        populateContactsSchedulesReport();
    }
}
