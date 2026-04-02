package com.drone.delivery.drones.dto;

import com.drone.delivery.drones.enums.DroneModel;
import com.drone.delivery.drones.enums.DroneState;
import lombok.Data;
@Data
public class DroneResponse {
    private Long id;
    private String serialNumber;
    private DroneModel model;
    private int weightLimit;
    private int batteryCapacity;
    private DroneState state;
}
