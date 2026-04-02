package com.drone.delivery.drones.repository;

import com.drone.delivery.drones.entity.Drone;
import com.drone.delivery.drones.enums.DroneState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DroneRepository extends JpaRepository<Drone, Long> {
    List<Drone> findByState(DroneState state);
}
