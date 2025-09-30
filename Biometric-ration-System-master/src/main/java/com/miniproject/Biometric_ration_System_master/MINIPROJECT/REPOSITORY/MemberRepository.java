package com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, String> {

    Optional<Member> findByAadharNumber(String aadharNumber);

    @Query("{ 'memberId': ?0 }")
    Optional<Member> findByMemberId(Integer memberId);

    @Query("{ 'householdId': ?0 }")
    List<Member> findByHouseholdId(String householdId);


}
