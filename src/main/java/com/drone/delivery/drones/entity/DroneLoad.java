package com.drone.delivery.drones.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class DroneLoad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Drone drone;
    @ManyToOne
    private Medication medication;
}
