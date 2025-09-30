package com.miniproject.Biometric_ration_System_master.MINIPROJECT.CONTROLLER;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES.HouseholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map; // ✅ ADDED this import for resolving 'Map'

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    // GET all households
    @GetMapping
    public ResponseEntity<List<Households>> getAllHouseholds() {
        List<Households> households = householdService.getAllHouseholds();
        if (households.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(households, HttpStatus.OK);
    }

    // GET household by ID
    @GetMapping("/{householdId}")
    public ResponseEntity<Households> getHouseholdById(@PathVariable String householdId) {
        Optional<Households> household = householdService.findByHouseholdId(householdId);
        return household.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST create new household
    @PostMapping
    public ResponseEntity<Households> createHousehold(@RequestBody Households household) {
        try {
            if (household.getFamilyHead() == null || household.getFamilyHead().trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Households savedHousehold = householdService.saveHousehold(household);
            return new ResponseEntity<>(savedHousehold, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update existing household
    @PutMapping("/{householdId}")
    public ResponseEntity<Households> updateHousehold(
            @PathVariable String householdId,
            @RequestBody Households households1) {
        Optional<Households> existingHousehold = householdService.findByHouseholdId(householdId);
        if (existingHousehold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Households household = existingHousehold.get();
            household.setFamilyHead(households1.getFamilyHead());
            household.setAllocatedRation(households1.getAllocatedRation());
            // 👉 You can separately update the member list if needed here

            Households updatedHousehold = householdService.saveHousehold(household);
            return new ResponseEntity<>(updatedHousehold, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE household by ID
    @DeleteMapping("/{householdId}")
    public ResponseEntity<Void> deleteHousehold(@PathVariable String householdId) {
        Optional<Households> household = householdService.findByHouseholdId(householdId);
        if (household.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            householdService.deleteHousehold(householdId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PATCH update ration status
    @PatchMapping("/{householdId}/ration-status")
    public ResponseEntity<Households> updateRationStatus(
            @PathVariable String householdId,
            @RequestParam boolean rationStatus) {
        Optional<Households> existingHousehold = householdService.findByHouseholdId(householdId);
        if (existingHousehold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Households household = existingHousehold.get();
            household.setRationStatus(rationStatus);
            Households updatedHousehold = householdService.saveHousehold(household);
            return new ResponseEntity<>(updatedHousehold, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
