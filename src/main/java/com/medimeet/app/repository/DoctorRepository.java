package com.medimeet.app.repository;

import com.medimeet.app.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
	List<Doctor> findBySpecialty(String specialty);
}