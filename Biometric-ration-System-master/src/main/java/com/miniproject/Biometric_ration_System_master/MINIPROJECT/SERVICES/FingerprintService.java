package com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Member;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY.HouseholdRepository;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FingerprintService {

    private final MemberRepository memberRepository;
    private final HouseholdRepository householdsRepository;

    @Autowired
    public FingerprintService(MemberRepository memberRepository, HouseholdRepository householdsRepository) {
        this.memberRepository = memberRepository;
        this.householdsRepository = householdsRepository;
    }

    // ✅ Match and return household info
    public Optional<Map<String, Object>> matchByMemberId(Integer memberId) {
        System.out.println("🔍 Searching for memberId: " + memberId);

        Optional<Member> matchedMember = memberRepository.findByMemberId(memberId);
        if (matchedMember.isEmpty()) {
            System.err.println("❌ No member found for memberId: " + memberId);
            return Optional.empty();
        }

        Member member = matchedMember.get();
        System.out.println("✅ Member found: " + member);

        String householdId = member.getHouseholdId();
        if (householdId == null) {
            System.err.println("❌ Household ID in member is null!");
            return Optional.empty();
        }

        System.out.println("🏠 Fetching household with ID: " + householdId);
        Optional<Households> householdOpt = householdsRepository.findByHouseholdId(householdId);

        if (householdOpt.isEmpty()) {
            System.err.println("❌ No household found with ID: " + householdId);
            return Optional.empty();
        }

        Households household = householdOpt.get();
        System.out.println("✅ Household found: " +  household.getHouseholdId());

        List<Member> allMembers = memberRepository.findByHouseholdId(householdId);
        System.out.println("👨‍👩‍👧 Members in this household: " + allMembers.size());

        Map<String, Object> response = new HashMap<>();
        response.put("matchedMember", member);
        response.put("household", household);
        response.put("familyMembers", allMembers);

        return Optional.of(response);
    }

    // ✅ Update ration status for a household (called after dispense)
    public boolean updateRationStatus(String householdId) {
        Optional<Households> householdOpt = householdsRepository.findByHouseholdId(householdId);
        if (householdOpt.isPresent()) {
            Households household = householdOpt.get();
            if (Boolean.TRUE.equals(household.getRationStatus())) {
                return false; // Already taken
            }
            household.setRationStatus(true);
            householdsRepository.save(household);
            return true;
        }
        return false;
    }
}
