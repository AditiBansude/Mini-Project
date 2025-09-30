package com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "households1")
public class Households {

    @Id
    private String id; // MongoDB auto-generated _id

    @Field("householdId")
    private String householdId;

    private String familyHead;

    private double allocatedRation;

    private boolean rationStatus = false;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public String getFamilyHead() {
        return familyHead;
    }

    public void setFamilyHead(String familyHead) {
        this.familyHead = familyHead;
    }

    public double getAllocatedRation() {
        return allocatedRation;
    }

    public void setAllocatedRation(double allocatedRation) {
        this.allocatedRation = allocatedRation;
    }

    public Boolean getRationStatus() {
        return rationStatus;
    }

    public void setRationStatus(Boolean rationStatus) {
        this.rationStatus = rationStatus;
    }


    @Override
    public String toString() {
        return "Households{" +
                "id='" + id + '\'' +
                ", householdId='" + householdId + '\'' +
                ", familyHead='" + familyHead + '\'' +
                ", allocatedRation=" + allocatedRation +
                ", rationStatus=" + rationStatus +
                '}';
    }
}
