package com.drone.delivery.drones;

import com.drone.delivery.drones.dto.DroneRegisterRequest;
import com.drone.delivery.drones.entity.Drone;
import com.drone.delivery.drones.entity.DroneLoad;
import com.drone.delivery.drones.entity.Medication;
import com.drone.delivery.drones.enums.DroneModel;
import com.drone.delivery.drones.enums.DroneState;
import com.drone.delivery.drones.exception.LowBatteryException;
import com.drone.delivery.drones.exception.OverweightException;
import com.drone.delivery.drones.repository.DroneLoadRepository;
import com.drone.delivery.drones.repository.DroneRepository;
import com.drone.delivery.drones.repository.MedicationRepository;
import com.drone.delivery.drones.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DroneServiceTest {
    private final DroneRepository droneRepository = mock(DroneRepository.class);
    private final MedicationRepository medicationRepository = mock(MedicationRepository.class);
    private final DroneLoadRepository droneLoadRepository = mock(DroneLoadRepository.class);
    private final DroneService service =
            new DroneService(droneRepository, medicationRepository, droneLoadRepository);

    @Test
    void testDroneRegistration() {
        DroneRegisterRequest req = new DroneRegisterRequest();
        req.setSerialNumber("DRN-9000");
        req.setModel(DroneModel.LIGHTWEIGHT);
        req.setWeightLimit(200);
        req.setBatteryCapacity(70);
        Drone saved = new Drone();
        saved.setId(1L);
        saved.setSerialNumber("DRN-9000");
        when(droneRepository.save(Mockito.any(Drone.class))).thenReturn(saved);
        var response = service.registerDrone(req);
        assertNotNull(response);
        assertEquals("DRN-9000", response.getSerialNumber());
    }

    @Test
    void testLoadMedication_SingleMedicationBelowWeightLimit() {
        // Setup: Create drone with IDLE state and weight limit of 300
        Drone drone = new Drone();
        drone.setId(1L);
        drone.setSerialNumber("DRN-001");
        drone.setModel(DroneModel.MIDDLEWEIGHT);
        drone.setWeightLimit(300);
        drone.setBatteryCapacity(50);
        drone.setState(DroneState.IDLE);

        // Setup: Create medication with weight 100
        Medication medication = new Medication();
        medication.setId(1L);
        medication.setName("Aspirin");
        medication.setWeight(100);
        medication.setCode("ASP_001");

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));
        when(droneLoadRepository.findByDroneId(1L)).thenReturn(new ArrayList<>());
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(0);
        when(droneLoadRepository.save(Mockito.any(DroneLoad.class))).thenReturn(new DroneLoad());
        when(droneRepository.save(Mockito.any(Drone.class))).thenReturn(drone);

        // Execute
        String result = service.loadMedication(1L, 1L);

        // Verify
        assertEquals("Medication loaded successfully", result);
        assertEquals(DroneState.LOADING, drone.getState());
        verify(droneLoadRepository, times(1)).save(Mockito.any(DroneLoad.class));
        verify(droneRepository, times(1)).save(Mockito.any(Drone.class));
    }

    @Test
    void testLoadMedication_MultipleIncrementalLoads_StateTransitionToLoaded() {
        // Setup: Create drone with weight limit 300
        Drone drone = new Drone();
        drone.setId(1L);
        drone.setSerialNumber("DRN-002");
        drone.setWeightLimit(300);
        drone.setBatteryCapacity(60);
        drone.setState(DroneState.IDLE);

        // Setup: Create medications
        Medication med1 = new Medication();
        med1.setId(1L);
        med1.setName("Paracetamol");
        med1.setWeight(100);

        Medication med2 = new Medication();
        med2.setId(2L);
        med2.setName("Ibuprofen");
        med2.setWeight(100);

        Medication med3 = new Medication();
        med3.setId(3L);
        med3.setName("Vitamin_C");
        med3.setWeight(100);

        // Mock first load (empty → 100)
        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med1));
        when(droneLoadRepository.findByDroneId(1L)).thenReturn(new ArrayList<>());
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(0);
        when(droneLoadRepository.save(Mockito.any(DroneLoad.class))).thenReturn(new DroneLoad());
        when(droneRepository.save(Mockito.any(Drone.class))).thenReturn(drone);

        // First load: should transition to LOADING
        String result1 = service.loadMedication(1L, 1L);
        assertEquals("Medication loaded successfully", result1);
        assertEquals(DroneState.LOADING, drone.getState());

        // Mock second load (100 → 200)
        drone.setState(DroneState.LOADING);
        List<DroneLoad> loads1 = new ArrayList<>();
        DroneLoad load1 = new DroneLoad();
        load1.setMedication(med1);
        loads1.add(load1);

        when(droneLoadRepository.findByDroneId(1L)).thenReturn(loads1);
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(100);
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(med2));

        // Second load: should remain LOADING
        String result2 = service.loadMedication(1L, 2L);
        assertEquals("Medication loaded successfully", result2);
        assertEquals(DroneState.LOADING, drone.getState());

        // Mock third load (200 → 300 = exact limit)
        List<DroneLoad> loads2 = new ArrayList<>();
        loads2.add(load1);
        DroneLoad load2 = new DroneLoad();
        load2.setMedication(med2);
        loads2.add(load2);

        when(droneLoadRepository.findByDroneId(1L)).thenReturn(loads2);
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(200);
        when(medicationRepository.findById(3L)).thenReturn(Optional.of(med3));

        // Third load: should transition to LOADED (weight == limit)
        String result3 = service.loadMedication(1L, 3L);
        assertEquals("Medication loaded successfully", result3);
        assertEquals(DroneState.LOADED, drone.getState());
    }

    @Test
    void testLoadMedication_WeightExceeded_ThrowsOverweightException() {
        // Setup: Create drone with weight limit 200
        Drone drone = new Drone();
        drone.setId(1L);
        drone.setWeightLimit(200);
        drone.setBatteryCapacity(50);
        drone.setState(DroneState.IDLE);

        // Setup: Create medications
        Medication med1 = new Medication();
        med1.setId(1L);
        med1.setWeight(150);

        Medication med2 = new Medication();
        med2.setId(2L);
        med2.setWeight(100);

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(med1));
        when(medicationRepository.findById(2L)).thenReturn(Optional.of(med2));

        // Setup existing load: 150
        List<DroneLoad> existingLoads = new ArrayList<>();
        DroneLoad load1 = new DroneLoad();
        load1.setMedication(med1);
        existingLoads.add(load1);

        when(droneLoadRepository.findByDroneId(1L)).thenReturn(existingLoads);
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(150);

        // Try to load medication that would exceed limit (150 + 100 = 250 > 200)
        assertThrows(OverweightException.class, () -> service.loadMedication(1L, 2L));

        // Verify no save was called
        verify(droneLoadRepository, never()).save(Mockito.any(DroneLoad.class));
    }

    @Test
    void testLoadMedication_LowBattery_ThrowsLowBatteryException() {
        // Setup: Create drone with battery < 25%
        Drone drone = new Drone();
        drone.setId(1L);
        drone.setBatteryCapacity(20);
        drone.setWeightLimit(300);
        drone.setState(DroneState.IDLE);

        Medication medication = new Medication();
        medication.setId(1L);
        medication.setWeight(50);

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

        // Should throw before checking medication or weight
        assertThrows(LowBatteryException.class, () -> service.loadMedication(1L, 1L));

        verify(medicationRepository, never()).findById(Mockito.anyLong());
        verify(droneLoadRepository, never()).save(Mockito.any(DroneLoad.class));
    }

    @Test
    void testLoadMedication_StreamBasedWeightCalculationCorrect() {
        // This test verifies the stream-based weight calculation matches query-based result
        Drone drone = new Drone();
        drone.setId(1L);
        drone.setWeightLimit(500);
        drone.setBatteryCapacity(50);
        drone.setState(DroneState.IDLE);

        Medication med1 = new Medication();
        med1.setId(1L);
        med1.setWeight(75);

        Medication med2 = new Medication();
        med2.setId(2L);
        med2.setWeight(125);

        Medication med3 = new Medication();
        med3.setId(3L);
        med3.setWeight(50);

        // Create drone loads: 75 + 125 = 200
        List<DroneLoad> existingLoads = new ArrayList<>();
        DroneLoad load1 = new DroneLoad();
        load1.setMedication(med1);
        DroneLoad load2 = new DroneLoad();
        load2.setMedication(med2);
        existingLoads.add(load1);
        existingLoads.add(load2);

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationRepository.findById(3L)).thenReturn(Optional.of(med3));
        when(droneLoadRepository.findByDroneId(1L)).thenReturn(existingLoads);
        when(droneLoadRepository.findTotalWeightByDroneId(1L)).thenReturn(200); // Query result
        when(droneLoadRepository.save(Mockito.any(DroneLoad.class))).thenReturn(new DroneLoad());
        when(droneRepository.save(Mockito.any(Drone.class))).thenReturn(drone);

        // Load medication: 200 + 50 = 250 (below 500 limit)
        String result = service.loadMedication(1L, 3L);

        assertEquals("Medication loaded successfully", result);
        assertEquals(DroneState.LOADING, drone.getState()); // 250 < 500 limit
        verify(droneLoadRepository, times(1)).findTotalWeightByDroneId(1L);
        verify(droneLoadRepository, times(1)).findByDroneId(1L);
    }
}
