package com.medimeet.app;

import com.medimeet.app.model.Doctor;
import com.medimeet.app.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(DoctorRepository repository) {
		return args -> {
			// Only add if no doctors exist
			if (repository.count() == 0) {
				repository.saveAll(Arrays.asList(
					new Doctor("1", "Dr. Alice Smith", "Cardiology"),
					new Doctor("2", "Dr. John Doe", "Neurology"),
					new Doctor("3", "Dr. Jane Miller", "Dermatology"),
					new Doctor("4", "Dr. Michael Brown", "Pediatrics")
				));
			}
		};
	}
}
