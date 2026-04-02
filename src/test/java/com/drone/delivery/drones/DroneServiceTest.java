package com.drone.delivery.drones;

import com.drone.delivery.drones.dto.DroneRegisterRequest;
import com.drone.delivery.drones.entity.Drone;
import com.drone.delivery.drones.enums.DroneModel;
import com.drone.delivery.drones.repository.DroneLoadRepository;
import com.drone.delivery.drones.repository.DroneRepository;
import com.drone.delivery.drones.repository.MedicationRepository;
import com.drone.delivery.drones.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class DroneServiceTest {
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
}
