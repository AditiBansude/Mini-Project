package com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Households;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Member;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY.HouseholdRepository;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.REPOSITORY.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    // Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Get member by ID
    public Optional<Member> getMemberById(Integer memberId) {
        return memberRepository.findByMemberId(memberId);
    }



    // Get member by Aadhar number
    public Optional<Member> getMemberByAadhar(String aadharNumber) {
        return memberRepository.findByAadharNumber(aadharNumber);
    }

    // Create a new member
    public Member createMember(Member member) {
        // No need to set householdId again if it's already an Integer
        return memberRepository.save(member);
    }

    // Update an existing member
    public Member updateMember(Member updatedMember) {
        return memberRepository.save(updatedMember);
    }

    // Delete member by ID
    public void deleteMember(Integer memberId) {
        Optional<Member> memberOpt = memberRepository.findByMemberId(memberId);
        if (memberOpt.isPresent()) {
            memberRepository.delete(memberOpt.get());
        } else {
            throw new RuntimeException("Member with ID " + memberId + " not found.");
        }
    }
    public List<Member> getFamilyMembers(String householdId) {
        return memberRepository.findByHouseholdId(householdId);
    }
    public Optional<Member> findByMemberId(Integer memberId) {
        return memberRepository.findByMemberId(memberId);
    }
    public List<Member> getMembersByHouseholdId(String householdId) {
        return memberRepository.findByHouseholdId(householdId);
    }


}
