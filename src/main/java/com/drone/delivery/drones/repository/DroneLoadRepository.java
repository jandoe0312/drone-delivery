package com.drone.delivery.drones.repository;


import com.drone.delivery.drones.entity.DroneLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DroneLoadRepository extends JpaRepository<DroneLoad, Long> {
    List<DroneLoad> findByDroneId(Long droneId);
}
