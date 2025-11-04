package controller;

import javafx.collections.ObservableList;
import model.dto.RoomInfoDTO;

import java.sql.*;

public class DashBoardController implements DashBoardService {

    @Override
    public void addRoomDetails(String roomId, String type, double pricePerNight, int maxGuests, boolean availability, String description, int floor) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation_system", "root", "1234");

            String SQL = "INSERT INTO rooms VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, roomId);
            preparedStatement.setObject(2, type);
            preparedStatement.setObject(3, pricePerNight);
            preparedStatement.setObject(4, maxGuests);
            preparedStatement.setObject(5, availability);
            preparedStatement.setObject(6, description);
            preparedStatement.setObject(7, floor);

            preparedStatement.executeUpdate();

        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteRoomDetails(String roomId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation_system", "root", "1234");
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM rooms WHERE room_id = ?");

            pstm.setObject(1,roomId);
            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ObservableList<RoomInfoDTO> getAllRoomsDetails() {

        ObservableList<RoomInfoDTO> roomDetails = javafx.collections.FXCollections.observableArrayList();

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_reservation_system", "root", "1234");
            String SQL = "SELECT * FROM rooms";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                roomDetails.add(new RoomInfoDTO(
                        // column name pass
                        resultSet.getString("room_id"),
                        resultSet.getString("type"),
                        resultSet.getDouble("price_per_night"),
                        resultSet.getInt("max_guests"),
                        resultSet.getBoolean("availability"),
                        resultSet.getString("description"),
                        resultSet.getInt("floor")
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roomDetails;
    }

    public void updateRoomDetails(String roomId, String type, double pricePerNight, int maxGuests, boolean availability, String description, int floor) {

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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

