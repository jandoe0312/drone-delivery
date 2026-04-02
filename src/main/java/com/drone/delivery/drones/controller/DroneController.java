package com.drone.delivery.drones.controller;

import com.drone.delivery.drones.dto.DroneRegisterRequest;
import com.drone.delivery.drones.dto.DroneResponse;
import com.drone.delivery.drones.dto.DroneLoadResponse;
import com.drone.delivery.drones.dto.MedicationLoadRequest;
import com.drone.delivery.drones.service.DroneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/drones")
@RequiredArgsConstructor
public class DroneController {
    private final DroneService droneService;
    @PostMapping("/register")
    public ResponseEntity<DroneResponse> registerDrone(@Valid @RequestBody DroneRegisterRequest request) {
        return ResponseEntity.ok(droneService.registerDrone(request));
    }
    @PostMapping("/load")
    public ResponseEntity<String> loadMedication(@Valid @RequestBody MedicationLoadRequest request) {
        return ResponseEntity.ok(droneService.loadMedication(request.getDroneId(), request.getMedicationId()));
    }
    @GetMapping("/{droneId}/medications")
    public ResponseEntity<List<DroneLoadResponse>> getLoadedMedications(@PathVariable Long droneId) {
        return ResponseEntity.ok(droneService.getLoadedMedications(droneId));
    }
    @GetMapping("/available")
    public ResponseEntity<List<DroneResponse>> getAvailableDrones() {
        return ResponseEntity.ok(droneService.getAvailableDrones());
    }
    @GetMapping("/{droneId}/battery")
    public ResponseEntity<Integer> checkBattery(@PathVariable Long droneId) {
        return ResponseEntity.ok(droneService.checkBattery(droneId));
    }
}
