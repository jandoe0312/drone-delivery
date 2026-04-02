package com.drone.delivery.drones.entity;

import com.drone.delivery.drones.enums.DroneModel;
import com.drone.delivery.drones.enums.DroneState;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Entity
@Data
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String serialNumber;
    @Enumerated(EnumType.STRING)
    private DroneModel model;
    @Max(1000)
    private int weightLimit;
    @Min(0)
    @Max(100)
    private int batteryCapacity;
    @Enumerated(EnumType.STRING)
    private DroneState state;
}