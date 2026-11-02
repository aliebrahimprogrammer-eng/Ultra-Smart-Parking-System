package com.ga.parking.model;

import com.ga.parking.model.enums.SpotStatus;
import com.ga.parking.model.enums.SpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_spots")
public class ParkingSpot {

    @Id
    private String id;

    private String lotId;

    private String spotNumber;

    private SpotType type;

    private SpotStatus status;

    @Version
    private Long version;
}
