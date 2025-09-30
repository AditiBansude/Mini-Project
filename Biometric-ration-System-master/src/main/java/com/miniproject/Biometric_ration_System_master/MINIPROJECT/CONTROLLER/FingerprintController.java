package com.miniproject.Biometric_ration_System_master.MINIPROJECT.CONTROLLER;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.FingerprintData;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES.FingerprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/fingerprint")
public class FingerprintController {

    private final FingerprintService fingerprintService;

    @Autowired
    public FingerprintController(FingerprintService fingerprintService) {
        this.fingerprintService = fingerprintService;
    }
    @PostMapping("/match")
    public ResponseEntity<?> matchByMemberId(@RequestBody FingerprintData data) {
        if (data == null || data.getMemberId() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: Member ID is required and cannot be null.");
        }


        try {
            Optional<Map<String, Object>> household = fingerprintService.matchByMemberId(data.getMemberId());

            if (household.isPresent()) {
                return ResponseEntity.ok(household.get());
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No household found for the provided Member ID.");
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while matching fingerprint: " + e.getMessage());
        }
    }
    @PatchMapping("/updateStatus/{householdId}")
    public ResponseEntity<String> updateRationStatus(@PathVariable String householdId) {
        try {
            boolean updated = fingerprintService.updateRationStatus(householdId);
            if (updated) {
                return ResponseEntity.ok("Ration status updated to 'distributed'");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Household not found or already distributed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating ration status: " + e.getMessage());
        }
    }


}
