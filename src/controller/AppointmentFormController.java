package controller;

import helper.dbaccess.dao.DBAppointment;
import helper.dbaccess.dao.DBContact;
import helper.dbaccess.dao.DBCustomer;
import helper.dbaccess.dao.DBUser;
import helper.locale.LocaleHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The appointment form controller that displays all the editable fields a blank, new appointment or an already-filled-out, existing appointment.
 * Validates all data given by user and refuses to save if data is not valid.
 */
public class AppointmentFormController implements Initializable {
    /**
     * Represents the creation of a new appointment or the editing of an existing appointment.
     */
    private enum AppointmentOperation { CREATE, EDIT }

    /**
     * The exception that is thrown if one or more fields are blank when trying to save the appointment.
     */
    private static class FieldBlankException extends Exception {}

    /**
     * The exception that is thrown if an appointment already exists that would interfere with
     * this appointment's date &amp; time.
     */
    private static class ConflictingAppointmentTimeException extends Throwable {}

    /**
     * The exception that is thrown if the end time is before the start time.
     */
    private static class EndTimeIsBeforeStartTimeException extends Throwable {}
    /**
     * The exception that is thrown if either the startsAt or endsAt time for the appointment are out of bounds for office hours.
     */
    private static class OutsideOfOfficeHoursException extends Throwable {}

    public static final String viewFilename = "AppointmentForm.fxml";
    public static final String desiredStageTitle = "Appointment";
    public Stage currentStage;
    public MainController parentController;

    private Appointment appointment;
    private AppointmentOperation currentAppointmentOperation = AppointmentOperation.CREATE;

    private final ObservableList<Appointment> conflictingAppointments = FXCollections.observableArrayList();

    private final ObservableMap<String, Integer> contactsNameToIdMap = FXCollections.observableHashMap();
    private final ObservableMap<String, Integer> customersNameToIdMap = FXCollections.observableHashMap();
    private final ObservableMap<String, Integer> usersUsernameToIdMap = FXCollections.observableHashMap();

    public Label appointmentLabel;
    public TextField idTextField;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField locationTextField;
    public TextField typeTextField;
    public ChoiceBox<String> contactNameChoiceBox;
    public ChoiceBox<String> customerNameChoiceBox;
    public ChoiceBox<String> userUsernameChoiceBox;
    public DatePicker appointmentDateDatePicker;
    public TextField startsAtTimeTextField;
    public TextField endsAtTimeTextField;

    /**
     * Initializes the AppointmentFormController.
     * @param url the URL
     * @param resourceBundle the ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactsNameToIdMap.clear();
        customersNameToIdMap.clear();
        usersUsernameToIdMap.clear();
        contactNameChoiceBox.getItems().clear();
        customerNameChoiceBox.getItems().clear();
        userUsernameChoiceBox.getItems().clear();
        for (Contact contact : DBContact.getAllContacts()) {
            contactsNameToIdMap.put(contact.getName(), contact.getId());
            contactNameChoiceBox.getItems().add(contact.getName());
        }
        for (Customer customer : DBCustomer.getAllCustomers()) {
            customersNameToIdMap.put(customer.getName(), customer.getId());
            customerNameChoiceBox.getItems().add(customer.getName());
        }
        for (User user : DBUser.getAllUsers()) {
            usersUsernameToIdMap.put(user.getUsername(), user.getId());
            userUsernameChoiceBox.getItems().add(user.getUsername());
        }
    }

    /**
     * Invoked by the MainController when the "Add" button is clicked, and sets the fields &amp; labels accordingly.
     */
    public void addNewAppointment() {
        appointmentLabel.setText("New Appointment");
    }

    /**
     * Invoked by the MainController when the "Modify" button is clicked, and sets the fields &amp; labels + currentAppointmentOperation accordingly.
     * @param existingAppointment the appointment that should be edited.
     */
    public void editExistingAppointment(Appointment existingAppointment) {
        this.appointment = existingAppointment;
        this.currentAppointmentOperation = AppointmentOperation.EDIT;

        appointmentLabel.setText("Appointment " + appointment.getId());
        idTextField.setText(String.valueOf(appointment.getId()));
        titleTextField.setText(appointment.getTitle());
        descriptionTextField.setText(appointment.getDescription());
        locationTextField.setText(appointment.getLocation());
        typeTextField.setText(appointment.getType());
        appointmentDateDatePicker.setValue(LocalDate.ofInstant(existingAppointment.getStartsAt(), LocaleHelper.getZoneId()));
        startsAtTimeTextField.setText(LocalTime.ofInstant(existingAppointment.getStartsAt(), LocaleHelper.getZoneId()).format(DateTimeFormatter.ISO_LOCAL_TIME));
        endsAtTimeTextField.setText(LocalTime.ofInstant(existingAppointment.getEndsAt(), LocaleHelper.getZoneId()).format(DateTimeFormatter.ISO_LOCAL_TIME));
        try {
            contactNameChoiceBox.setValue(appointment.getContactName());
            customerNameChoiceBox.setValue(appointment.getCustomerName());
            userUsernameChoiceBox.setValue(appointment.getUserUsername());
        } catch (Appointment.ContactNotFoundException | Appointment.CustomerNotFoundException | Appointment.UserNotFoundException e) {
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
     * Invoked when the "Save" button is pressed, and closes the modal after saving the appointment after validating the user input.
     * @see #checkFields()
     * @see #updateAppointment()
     * @see #createAppointment()
     * @see MainController#updateAppointment()
     * @see MainController#addAppointment()
     * @see #showErrorAlert(String, String)
     */
    public void onSave() {
        try {
            checkFields();
            if (currentAppointmentOperation == AppointmentOperation.EDIT) {
                updateAppointment();
                parentController.updateAppointment();
            } else if (currentAppointmentOperation == AppointmentOperation.CREATE) {
                createAppointment();
                parentController.addAppointment();
            }

            currentStage.close();
        } catch (FieldBlankException e) {
            e.printStackTrace();
            showErrorAlert("Fields Blank!",
                    "One or more of the fields of the form are blank. Please fill them out and try again.");
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            showErrorAlert("Incorrectly Formatted Times!",
                    "One or both of the provided times are not formatted correctly." +
                            " Please enter times as a standard 12-hour or 24-hour time format and try again.");
        } catch (EndTimeIsBeforeStartTimeException e) {
            e.printStackTrace();
            showErrorAlert("Invalid Start and End Times!",
                    "The starting time for the appointment (including the date) is not before the ending time." +"" +
                            " The ending time must occur after the starting time.");
        } catch (OutsideOfOfficeHoursException e) {
            e.printStackTrace();
            showErrorAlert("Appointment Time Outside Office Hours!",
                    "The appointment time occurs outside of office hours. Office hours are from 08:00 AM-10:00 PM EST every day.");
        } catch (ConflictingAppointmentTimeException e) {
            e.printStackTrace();
            String conflictingAppointmentsString = conflictingAppointments.stream().map(Appointment::toPrettyString).collect(Collectors.joining(",\n"));
            showErrorAlert("Appointment Times Conflict!",
                    "One or more appointments conflict with the specified appointment times. The appointments are:\n" +
                            conflictingAppointmentsString + ".");
        }
    }

    /**
     * Applies the fields to a new appointment model and saves to the database.
     * <br>
     * The lambda allows us to use 1 line of code to assign the returned Optional&lt;Appointment&gt; if it is present.
     * This saves 2 lines of code.
     * @see #applyFieldsToAppointment()
     * @see DBAppointment
     */
    private void createAppointment() {
        appointment = new Appointment();
        applyFieldsToAppointment();
        Optional<Appointment> foundAppointment = DBAppointment.createAppointment(appointment);
        foundAppointment.ifPresent(value -> appointment = value);
    }

    /**
     * Applies the fields to the appointment model and saves changes to the database
     * <br>
     * The lambda allows us to use 1 line of code to assign the returned Optional&lt;Appointment&gt; if it is present.
     * This saves 2 lines of code.
     * @see #appointment
     * @see DBAppointment
     */
    private void updateAppointment() {
        applyFieldsToAppointment();
        DBAppointment.updateAppointment(appointment);
        Optional<Appointment> foundAppointment = DBAppointment.getAppointmentFromId(appointment.getId());
        foundAppointment.ifPresent(value -> appointment = value);
    }

    /**
     * Takes all the user-input values in the form fields and sets the appropriate attributes in the appointment model.
     * @see #appointment
     */
    private void applyFieldsToAppointment() {
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String type = typeTextField.getText();
        String contactName = contactNameChoiceBox.getValue();
        String customerName = customerNameChoiceBox.getValue();
        String userUsername = userUsernameChoiceBox.getValue();
        LocalDate appointmentDate = appointmentDateDatePicker.getValue();
        String rawStartsAtTime = startsAtTimeTextField.getText();
        String rawEndsAtTime = endsAtTimeTextField.getText();

        int contactId = contactsNameToIdMap.get(contactName);
        int customerId = customersNameToIdMap.get(customerName);
        int userId = usersUsernameToIdMap.get(userUsername);
        LocalTime startsAtTime = LocalTime.parse(rawStartsAtTime);
        LocalTime endsAtTime = LocalTime.parse(rawEndsAtTime);
        Instant startsAtInstant = appointmentDate.atTime(startsAtTime).atZone(LocaleHelper.getZoneId()).toInstant();
        Instant endsAtInstant = appointmentDate.atTime(endsAtTime).atZone(LocaleHelper.getZoneId()).toInstant();

        appointment.setContactId(contactId);
        appointment.setCustomerId(customerId);
        appointment.setUserId(userId);
        appointment.setTitle(title);
        appointment.setDescription(description);
        appointment.setLocation(location);
        appointment.setType(type);
        appointment.setStartsAt(startsAtInstant);
        appointment.setEndsAt(endsAtInstant);
    }

    /**
     * Checks the user input and verifies that:
     * <ul>
     * <li>no fields are blank;</li>
     * <li>the time values are formatted correctly;</li>
     * <li>the starting time is before the ending time;</li>
     * <li>the appointment times do not conflict with another appointment; or</li>
     * <li>the appointment is within office hours.</li>
     * </ul>
     * <br>
     * The lambda in this method removes the current appointment from the list of conflicting appointments.
     * This is necessary if an appointment is being edited, as an appointment has the possibility of having
     * its new times conflict with its old times.
     * @throws FieldBlankException if any fields are blank.
     * @throws DateTimeParseException if either of the time values are formatted incorrectly.
     * @throws EndTimeIsBeforeStartTimeException if the starting time is not before the ending time.
     * @throws ConflictingAppointmentTimeException if the appointment times conflict with another appointment.
     * @throws OutsideOfOfficeHoursException if the appointment goes outside of office hours.
     */
    private void checkFields() throws FieldBlankException, DateTimeParseException, EndTimeIsBeforeStartTimeException, ConflictingAppointmentTimeException, OutsideOfOfficeHoursException {
        String rawTitle = titleTextField.getText();
        String rawDescription = descriptionTextField.getText();
        String rawLocation = locationTextField.getText();
        String rawType = typeTextField.getText();
        String rawContactName = contactNameChoiceBox.getValue();
        String rawCustomerName = customerNameChoiceBox.getValue();
        String rawUserUsername = userUsernameChoiceBox.getValue();
        LocalDate appointmentDate = appointmentDateDatePicker.getValue();
        String rawStartsAtTime = startsAtTimeTextField.getText();
        String rawEndsAtTime = endsAtTimeTextField.getText();
        if (rawContactName == null || rawCustomerName == null || rawUserUsername == null || appointmentDate == null || rawTitle.isBlank() || rawDescription.isBlank() || rawLocation.isBlank() || rawType.isBlank() || rawContactName.isBlank() || rawCustomerName.isBlank() || rawStartsAtTime.isBlank() || rawUserUsername.isBlank() || rawEndsAtTime.isBlank()) {
            throw new FieldBlankException();
        }

        LocalTime startsAtLocalTime = LocalTime.parse(rawStartsAtTime);
        LocalTime endsAtLocalTime = LocalTime.parse(rawEndsAtTime);
        OffsetTime startsAtOffsetTime = startsAtLocalTime.atOffset(LocaleHelper.getZoneId().getRules().getOffset(Instant.now()));
        OffsetTime endsAtOffsetTime = endsAtLocalTime.atOffset(LocaleHelper.getZoneId().getRules().getOffset(Instant.now()));
        if (!startsAtOffsetTime.isBefore(endsAtOffsetTime)) {
            throw new EndTimeIsBeforeStartTimeException();
        }
        if (Appointment.isOutsideOfficeHours(startsAtOffsetTime) || Appointment.isOutsideOfficeHours(endsAtOffsetTime)) {
            throw new OutsideOfOfficeHoursException();
        }
        Instant startsAtInstant = appointmentDate.atTime(startsAtOffsetTime).toInstant();
        Instant endsAtInstant = appointmentDate.atTime(endsAtOffsetTime).toInstant();
        conflictingAppointments.setAll(DBAppointment.getAllAppointmentsOverlappingWithTimeRange(startsAtInstant, endsAtInstant));
        if (appointment != null) {
            conflictingAppointments.removeIf(appt -> appt.getId() == appointment.getId());
        }
        if (!conflictingAppointments.isEmpty()) {
            throw new ConflictingAppointmentTimeException();
        }
    }
}
