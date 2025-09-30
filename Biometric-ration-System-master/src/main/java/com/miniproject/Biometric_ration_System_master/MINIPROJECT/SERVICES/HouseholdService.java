package com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    // Get all households
    public List<Households> getAllHouseholds() {
        return householdRepository.findAll();
    }

    // Get household by ID
    public Optional<Households> findByHouseholdId(String householdId) {
        return householdRepository.findByHouseholdId(householdId);
    }

    // Save or update household
    public Households saveHousehold(Households household) {
        return householdRepository.save(household);
    }

    // Delete household by ID
    // Optional improvement - instead of deleteByHouseholdId
    public void deleteHousehold(String householdId) {
        householdRepository.deleteByHouseholdId(householdId);
    }



}
