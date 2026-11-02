package com.ga.parking.model;

import com.ga.parking.model.enums.SpotStatus;
import com.ga.parking.model.enums.SpotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "parking_spots")
@CompoundIndex(
        name = "unique_spot_per_lot",
        def = "{'lotId': 1, 'spotNumber': 1}",
        unique = true
)
public class ParkingSpot {

    @Id
    private String id;

    @NotBlank(message = "Parking lot ID is required")
    private String lotId;

    @NotBlank(message = "Spot number is required")
    private String spotNumber;

    @NotNull(message = "Spot type is required")
    private SpotType type;

    private SpotStatus status;

    @Version
    private Long version;
}
