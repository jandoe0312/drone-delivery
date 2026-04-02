package com.drone.delivery.drones.dto;

import com.drone.delivery.drones.enums.DroneModel;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class DroneRegisterRequest {
    @NotBlank
    @Size(max = 100)
    private String serialNumber;
    @NotNull
    private DroneModel model;
    @Max(1000)
    @Min(1)
    private int weightLimit;
    @Min(0)
    @Max(100)
    private int batteryCapacity;
}
