package com.ga.parking.model;

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

    private String name;

    private String location;

    private Double hourlyRate;

    private Integer totalSpots;
}
