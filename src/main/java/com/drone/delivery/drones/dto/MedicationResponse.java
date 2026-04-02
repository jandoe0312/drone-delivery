package com.drone.delivery.drones.dto;

import lombok.Data;
@Data
public class MedicationResponse {
    private Long id;
    private String name;
    private int weight;
    private String code;
}
