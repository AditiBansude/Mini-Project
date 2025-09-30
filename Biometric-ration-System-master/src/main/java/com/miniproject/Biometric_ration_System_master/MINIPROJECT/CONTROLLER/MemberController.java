package com.miniproject.Biometric_ration_System_master.MINIPROJECT.CONTROLLER;

import com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES.Member;
import com.miniproject.Biometric_ration_System_master.MINIPROJECT.SERVICES.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // GET all members
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return members.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(members, HttpStatus.OK);
    }

    // GET member by ID
    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMemberById(@PathVariable Integer memberId) {
        Optional<Member> member = memberService.getMemberById(memberId);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET member by Aadhar number
    @GetMapping("/aadhar/{aadharNumber}")
    public ResponseEntity<Member> getMemberByAadhar(@PathVariable String aadharNumber) {
        Optional<Member> member = memberService.getMemberByAadhar(aadharNumber);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET member by fingerprint (by member ID)
    @GetMapping("/fingerprint/memberId/{memberId}")
    public ResponseEntity<Member> getMemberByFingerprint(@PathVariable Integer memberId) {
        Optional<Member> member = memberService.getMemberById(memberId);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/family-members/{memberId}")
    public ResponseEntity<List<Member>> getFamilyByMemberId(@PathVariable Integer memberId) {
        Optional<Member> member = memberService.findByMemberId(memberId);
        if (member.isPresent()) {
            String householdId = member.get().getHouseholdId();
            List<Member> family = memberService.getMembersByHouseholdId(householdId);
            return ResponseEntity.ok(family);
        }
        return ResponseEntity.notFound().build();
    }


    // POST create new member
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        try {
            if (member.getAadharNumber() == null || member.getAadharNumber().length() != 12) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid Aadhar
            }
            Member savedMember = memberService.createMember(member);
            return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error saving member: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update existing member
    @PutMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(@PathVariable Integer memberId, @RequestBody Member memberRequest) {
        Optional<Member> existingMember = memberService.getMemberById(memberId);
        if (existingMember.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            Member member = existingMember.get();
            member.setFullName(memberRequest.getFullName());
            member.setAadharNumber(memberRequest.getAadharNumber());
            member.setContactNumber(memberRequest.getContactNumber());
            member.setHouseholdId(memberRequest.getHouseholdId()); // ✅ use this


            Member updatedMember = memberService.updateMember(member);
            return new ResponseEntity<>(updatedMember, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error updating member: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE member
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Integer memberId) {
        Optional<Member> member = memberService.getMemberById(memberId);
        if (member.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            memberService.deleteMember(memberId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
