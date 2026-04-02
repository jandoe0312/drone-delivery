package com.drone.delivery.drones.service;

import com.drone.delivery.drones.entity.Drone;
import com.drone.delivery.drones.enums.DroneState;
import com.drone.delivery.drones.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final DroneRepository droneRepository;
    private final Random random = new Random();
    @Scheduled(fixedRate = 10000)
    public void updateDroneStates() {
        for (Drone drone : droneRepository.findAll()) {
            // Simulate battery drain between 1–5%
            int drain = random.nextInt(5) + 1;
            int newBattery = Math.max(0, drone.getBatteryCapacity() - drain);
            drone.setBatteryCapacity(newBattery);
            switch (drone.getState()) {
                case LOADED -> drone.setState(DroneState.DELIVERING);
                case DELIVERING -> drone.setState(DroneState.DELIVERED);
                case DELIVERED -> drone.setState(DroneState.RETURNING);
                case RETURNING -> drone.setState(DroneState.IDLE);
                default -> {
                }
            }
            droneRepository.save(drone);
        }
    }
}
