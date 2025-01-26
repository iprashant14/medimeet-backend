package com.medimeet.app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String name;
    private String specialty;
    private List<LocalDateTime> availableSlots;

    public Doctor() {
    }

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }
}
