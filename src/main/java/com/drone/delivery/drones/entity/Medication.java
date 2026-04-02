package com.drone.delivery.drones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
@Entity
@Data
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Pattern(regexp = "^[A-Za-z0-9_-]+$")
    private String name;
    private int weight;
    @Pattern(regexp = "^[A-Z0-9_]+$")
    private String code;
    @Lob
    private byte[] image;
}
