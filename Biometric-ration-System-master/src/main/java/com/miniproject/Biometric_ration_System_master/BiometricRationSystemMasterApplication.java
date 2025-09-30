package com.miniproject.Biometric_ration_System_master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.miniproject.Biometric_ration_System_master")
public class BiometricRationSystemMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiometricRationSystemMasterApplication.class, args);
	}
}
