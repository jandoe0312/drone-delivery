package com.drone.delivery.drones.service;

import com.drone.delivery.drones.dto.DroneRegisterRequest;
import com.drone.delivery.drones.dto.DroneResponse;
import com.drone.delivery.drones.dto.DroneLoadResponse;
import com.drone.delivery.drones.entity.Drone;
import com.drone.delivery.drones.entity.DroneLoad;
import com.drone.delivery.drones.entity.Medication;
import com.drone.delivery.drones.enums.DroneState;
import com.drone.delivery.drones.exception.*;
import com.drone.delivery.drones.repository.DroneLoadRepository;
import com.drone.delivery.drones.repository.DroneRepository;
import com.drone.delivery.drones.repository.MedicationRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class DroneService {
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    private final DroneLoadRepository droneLoadRepository;
    public DroneResponse registerDrone(DroneRegisterRequest request) {
        Drone drone = new Drone();
        drone.setSerialNumber(request.getSerialNumber());
        drone.setModel(request.getModel());
        drone.setWeightLimit(request.getWeightLimit());
        drone.setBatteryCapacity(request.getBatteryCapacity());
        drone.setState(DroneState.IDLE);
        droneRepository.save(drone);
        DroneResponse response = new DroneResponse();
        response.setId(drone.getId());
        response.setSerialNumber(drone.getSerialNumber());
        response.setModel(drone.getModel());
        response.setWeightLimit(drone.getWeightLimit());
        response.setBatteryCapacity(drone.getBatteryCapacity());
        response.setState(drone.getState());
        return response;
    }
    @Transactional
    public String loadMedication(Long droneId, Long medicationId) {
        Drone drone = droneRepository.findById(droneId)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found"));
        if (drone.getBatteryCapacity() < 25) {
            throw new LowBatteryException("Battery too low for LOADING (<25%)");
        }
        // Change state to LOADING only if drone is idle
        if (drone.getState() != DroneState.IDLE && drone.getState() != DroneState.LOADING) {
            throw new BadRequestException("Drone must be IDLE or LOADING to load medication");
        }
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new BadRequestException("Medication not found"));
        // Calculate current load
        int currentLoad = droneLoadRepository.findByDroneId(droneId)
                .stream()
                .map(dl -> dl.getMedication().getWeight())
                .reduce(0, Integer::sum);
        if (currentLoad + medication.getWeight() > drone.getWeightLimit()) {
            throw new OverweightException("Cannot load medication: weight limit exceeded");
        }
        // Save load
        DroneLoad load = new DroneLoad();
        load.setDrone(drone);
        load.setMedication(medication);
        droneLoadRepository.save(load);
        // Set state to LOADING or LOADED
        int updatedLoad = currentLoad + medication.getWeight();
        if (updatedLoad == drone.getWeightLimit()) {
            drone.setState(DroneState.LOADED);
        } else {
            drone.setState(DroneState.LOADING);
        }
        droneRepository.save(drone);
        return "Medication loaded successfully";
    }
    public List<DroneLoadResponse> getLoadedMedications(Long droneId) {
        return droneLoadRepository.findByDroneId(droneId)
                .stream()
                .map(dl -> {
                    DroneLoadResponse res = new DroneLoadResponse();
                    res.setDroneId(dl.getDrone().getId());
                    res.setDroneSerial(dl.getDrone().getSerialNumber());
                    res.setMedicationName(dl.getMedication().getName());
                    res.setCode(dl.getMedication().getCode());
                    res.setWeight(dl.getMedication().getWeight());
                    return res;
                })
                .collect(Collectors.toList());
    }
    public List<DroneResponse> getAvailableDrones() {
        return droneRepository.findByState(DroneState.IDLE)
                .stream()
                .map(d -> {
                    DroneResponse res = new DroneResponse();
                    res.setId(d.getId());
                    res.setSerialNumber(d.getSerialNumber());
                    res.setModel(d.getModel());
                    res.setBatteryCapacity(d.getBatteryCapacity());
                    res.setState(d.getState());
                    res.setWeightLimit(d.getWeightLimit());
                    return res;
                })
                .collect(Collectors.toList());
    }
    public int checkBattery(Long droneId) {
        return droneRepository.findById(droneId)
                .orElseThrow(() -> new DroneNotFoundException("Drone not found"))
                .getBatteryCapacity();
    }
}
