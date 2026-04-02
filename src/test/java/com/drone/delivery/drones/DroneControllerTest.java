package com.drone.delivery.drones;

import com.drone.delivery.drones.controller.DroneController;
import com.drone.delivery.drones.dto.DroneRegisterRequest;
import com.drone.delivery.drones.dto.DroneResponse;
import com.drone.delivery.drones.enums.DroneModel;
import com.drone.delivery.drones.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class DroneControllerTest {
    private final DroneService droneService = mock(DroneService.class);
    private final DroneController controller = new DroneController(droneService);
    @Test
    void testRegisterDroneEndpoint() {
        DroneRegisterRequest request = new DroneRegisterRequest();
        request.setSerialNumber("DRN-1001");
        request.setBatteryCapacity(50);
        request.setModel(DroneModel.LIGHTWEIGHT);
        request.setWeightLimit(300);
        DroneResponse mockResponse = new DroneResponse();
        mockResponse.setSerialNumber("DRN-1001");
        when(droneService.registerDrone(request)).thenReturn(mockResponse);
        ResponseEntity<DroneResponse> response = controller.registerDrone(request);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("DRN-1001", response.getBody().getSerialNumber());
    }
}
