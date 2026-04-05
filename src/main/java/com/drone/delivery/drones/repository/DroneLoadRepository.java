package com.drone.delivery.drones.repository;


import com.drone.delivery.drones.entity.DroneLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface DroneLoadRepository extends JpaRepository<DroneLoad, Long> {
    List<DroneLoad> findByDroneId(Long droneId);

    @Query("SELECT SUM(dl.medication.weight) FROM DroneLoad dl WHERE dl.drone.id = :droneId")
    Integer findTotalWeightByDroneId(@Param("droneId") Long droneId);
}
