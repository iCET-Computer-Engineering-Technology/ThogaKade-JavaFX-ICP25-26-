package controller;

import javafx.collections.ObservableList;
import model.dto.RoomInfoDTO;

public interface DashBoardService {

    void addRoomDetails(String roomId, String type, double pricePerNight, int maxGuests, boolean availability, String description, int floor);

    void deleteRoomDetails(String roomId);

    void updateRoomDetails(String roomId, String type, double pricePerNight, int maxGuests, boolean availability, String description, int floor);

    ObservableList<RoomInfoDTO> getAllRoomsDetails();
}
