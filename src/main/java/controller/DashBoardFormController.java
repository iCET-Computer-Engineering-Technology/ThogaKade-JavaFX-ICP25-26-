package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.dto.RoomInfoDTO;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Locale;

public class DashBoardFormController implements Initializable {



    DashBoardService dashBoardService = new DashBoardController();

    ObservableList<RoomInfoDTO> roomInfoDTOS = FXCollections.observableArrayList();


    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReload;

    @FXML
    private Button btnUpdate;

    @FXML
    private ComboBox<Integer> cmbFloor;

    @FXML
    private ComboBox<Integer> cmbMaxGuests;

    @FXML
    private ComboBox<String> cmbType;

    @FXML
    private TableColumn<?, ?> colAvailability;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colFloor;

    @FXML
    private TableColumn<?, ?> colMaxGuests;

    @FXML
    private TableColumn<?, ?> colPricePreNight;

    @FXML
    private TableColumn<?, ?> colRoomId;

    @FXML
    private TableColumn<?, ?> colType;

    @FXML
    private RadioButton radioAvailable;

    @FXML
    private RadioButton radioUnavailable;

    @FXML
    private TableView<RoomInfoDTO> tblRoomInfo;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtPricePerNight;

    @FXML
    private TextField txtRoomId;

    @FXML
    private Text txtTime;

    @FXML
    private Text txtDate;


    //-------------------------------initialize method------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //load table data on initialize
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPricePreNight.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colMaxGuests.setCellValueFactory(new PropertyValueFactory<>("maxGuests"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));

        loadRoomsDetails();

        //load combo box data
        ObservableList<String> roomTypes = FXCollections.observableArrayList("Single", "Double", "Suite", "Deluxe","Family");
        cmbType.setItems(roomTypes);

        ObservableList<Integer> maxGuestsOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6);
        cmbMaxGuests.setItems(maxGuestsOptions);

        ObservableList<Integer> floorOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        cmbFloor.setItems(floorOptions);

        //radio button toggle group
        ToggleGroup availabilityGroup = new ToggleGroup();
        radioAvailable.setToggleGroup(availabilityGroup);
        radioUnavailable.setToggleGroup(availabilityGroup);

        //set selected row data to the fields
       tblRoomInfo.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {;

           if (newValue != null) {
               setSelectedValue(newValue);
           }
       });
       loadDateAndTime();
    }

    //----------------------------------Button actions-----------------------------------------
    @FXML
    void btnAddOnAction(ActionEvent event) {

        String roomId = txtRoomId.getText();
        String type = cmbType.getValue();
        double pricePerNight = Double.parseDouble((txtPricePerNight.getText()));
        int maxGuests = cmbMaxGuests.getValue();
        boolean availability = checkAvailability();
        String description = txtDescription.getText();
        int floor = cmbFloor.getValue();


        dashBoardService.addRoomDetails(roomId, type, pricePerNight, maxGuests,availability, description, floor);

        clearFields();
        loadRoomsDetails();



    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {


        dashBoardService.deleteRoomDetails(txtRoomId.getText());
        clearFields();
        loadRoomsDetails();
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadRoomsDetails();
    }
    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        String roomId = txtRoomId.getText();
        String type = cmbType.getValue();
        double pricePerNight = Double.parseDouble((txtPricePerNight.getText()));
        int maxGuests = cmbMaxGuests.getValue();
        boolean availability = checkAvailability();
        String description = txtDescription.getText();
        int floor = cmbFloor.getValue();


        dashBoardService.updateRoomDetails(roomId, type, pricePerNight, maxGuests, availability, description, floor);
        clearFields();
        loadRoomsDetails();

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();

    }

    //--------------------------------------All methods------------------------------------

    //load all rooms method
    private void loadRoomsDetails() {

        roomInfoDTOS.clear();


        tblRoomInfo.setItems(dashBoardService.getAllRoomsDetails());
    }

    //cheack availability method
    private boolean checkAvailability() {
        if (radioAvailable.isSelected()) {
            return true;
        } else {
            return false;
        }
    }

    //clear all fields method
    public void clearFields() {
        txtRoomId.clear();
        cmbType.setValue(null);
        txtPricePerNight.clear();
        cmbMaxGuests.setValue(null);
        radioAvailable.setSelected(false);
        radioUnavailable.setSelected(false);
        txtDescription.clear();
        cmbFloor.setValue(null);
    }

    //set selected row data to the fields
    private void setSelectedValue(RoomInfoDTO selectedValue) {
        if(selectedValue == null){
            clearFields();
            return;
        }
        txtRoomId.setText(selectedValue.getRoomId());
        cmbType.setValue(selectedValue.getType());
        txtPricePerNight.setText(String.valueOf(selectedValue.getPricePerNight()));
        cmbMaxGuests.setValue(selectedValue.getMaxGuests());
        if (selectedValue.isAvailability()){
            radioAvailable.setSelected(true);
        }else{
            radioUnavailable.setSelected(true);
        }
        txtDescription.setText(selectedValue.getDescription());
        cmbFloor.setValue(selectedValue.getFloor());
    }
    //load date and time method
    private void loadDateAndTime() {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh : mm : ss a", Locale.ENGLISH);
        DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        Runnable updater = () -> {
            LocalDateTime now = LocalDateTime.now();
            txtTime.setText(now.format(timeFormatter));
            int day = now.getDayOfMonth();
            String dayWithSuffix = day + getDayOfMonthSuffix(day);
            String monthYear = now.format(monthYearFormatter);
            txtDate.setText(String.format("%s %s", String.format("%02d", day) + getDayOfMonthSuffix(day), monthYear));
        };

        updater.run();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updater.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String getDayOfMonthSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
