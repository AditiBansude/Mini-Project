package com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "members1")
public class Member {

    @Id
    private String id;


    private Integer memberId;

    @Field("fullName")
    private String fullName;

    @Field("aadharNumber")
    @Indexed(unique = true)
    private String aadharNumber;

    @Field("contactNumber")
    private String contactNumber;

    @Field("householdId")
    private String householdId; // just store the household ID

    // 🔴 REMOVE THESE:
    // @ManyToOne
    // @JoinColumn(name = "household_id")
    // private Households households;

    // ✅ GETTERS & SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", memberId=" + memberId +
                ", fullName='" + fullName + '\'' +
                ", aadharNumber='" + aadharNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", householdId='" + householdId + '\'' +
                '}';
    }
}
