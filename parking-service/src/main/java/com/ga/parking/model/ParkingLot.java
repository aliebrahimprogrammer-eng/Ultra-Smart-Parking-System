package com.ga.parking.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_lots")
public class ParkingLot {

    @Id
    private String id;

    @NotBlank(message = "Parking lot name is required")
    private String name;

    @NotBlank(message = "Parking lot location is required")
    private String location;

    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be greater than zero")
    private Double hourlyRate;

    @NotNull(message = "Total spots is required")
    @Positive(message = "Total spots must be greater than zero")
    private Integer totalSpots;
}
