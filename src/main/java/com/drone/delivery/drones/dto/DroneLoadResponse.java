package com.drone.delivery.drones.dto;

import lombok.Data;
@Data
public class DroneLoadResponse {
    private Long droneId;
    private String droneSerial;
    private String medicationName;
    private String code;
    private int weight;
}
