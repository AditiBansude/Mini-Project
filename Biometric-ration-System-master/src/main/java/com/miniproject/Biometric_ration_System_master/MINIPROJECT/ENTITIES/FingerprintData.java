package com.miniproject.Biometric_ration_System_master.MINIPROJECT.ENTITIES;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "fingerprint_data") // MongoDB collection name
public class FingerprintData {

    @Id
    private Integer memberId;  // Unique identifier for fingerprint (acts as MongoDB _id)


    // Explicit Getters and Setters (optional since @Data handles it)
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }


}
