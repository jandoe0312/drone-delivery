package com.drone.delivery.drones.repository;


import com.drone.delivery.drones.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
