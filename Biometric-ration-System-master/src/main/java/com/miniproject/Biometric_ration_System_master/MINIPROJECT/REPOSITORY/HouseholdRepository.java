package com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface HouseholdRepository extends MongoRepository<Households, String> {

    @Query("{ 'householdId': ?0 }")
    Optional<Households> findByHouseholdId(String householdId);

    void deleteByHouseholdId(String householdId); // Let Spring auto-implement this

}
