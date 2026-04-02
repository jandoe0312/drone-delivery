package com.drone.delivery.drones.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class MedicationLoadRequest {
    @NotNull
    private Long droneId;
    @NotNull
    private Long medicationId;
}