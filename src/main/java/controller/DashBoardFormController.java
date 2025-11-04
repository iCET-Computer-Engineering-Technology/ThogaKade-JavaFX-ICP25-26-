package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.dto.RoomInfoDTO;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashBoardFormController implements Initializable {

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
        ObservableList<String> roomTypes = FXCollections.observableArrayList("Single", "Double", "Suite", "Deluxe");
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

        DashBoardController dashBoardController = new DashBoardController();
        dashBoardController.addRoomDetails(roomId, type, pricePerNight, maxGuests,availability, description, floor);
        clearFields();
        loadRoomsDetails();



    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

        DashBoardController dashBoardController = new DashBoardController();
        dashBoardController.deleteRoomDetails(txtRoomId.getText());
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

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation_system", "root", "1234");

            String SQL = "UPDATE rooms SET type = ?, price_per_night = ?, max_guests = ?, availability = ?, description = ?, floor = ? WHERE room_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, type);
            preparedStatement.setObject(2, pricePerNight);
            preparedStatement.setObject(3, maxGuests);
            preparedStatement.setObject(4, availability);
            preparedStatement.setObject(5, description);
            preparedStatement.setObject(6, floor);
            preparedStatement.setObject(7, roomId);

            preparedStatement.executeUpdate();
            clearFields();
            loadRoomsDetails();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    //--------------------------------------All methods------------------------------------

    //load all rooms method
    private void loadRoomsDetails() {

        roomInfoDTOS.clear();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation_system", "root", "1234");
            String SQL = "SELECT * FROM rooms";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                RoomInfoDTO roomInfoDTO = new RoomInfoDTO(

                        // column name pass
                        resultSet.getString("room_id"),
                        resultSet.getString("type"),
                        resultSet.getDouble("price_per_night"),
                        resultSet.getInt("max_guests"),
                        resultSet.getBoolean("availability"),
                        resultSet.getString("description"),
                        resultSet.getInt("floor")
                );
//                System.out.println(roomInfoDTO);
                roomInfoDTOS.add(roomInfoDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tblRoomInfo.setItems(roomInfoDTOS);
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
}

