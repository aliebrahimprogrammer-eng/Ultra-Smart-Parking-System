package com.ga.reservation_service.service;

import com.ga.reservation_service.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ParkingClient {

    private static final Logger logger = LoggerFactory.getLogger(ParkingClient.class);

    private final RestClient restClient;
    private final JwtUtils jwtUtils;

    public ParkingClient(
            @Value("${parking-service.url}") String parkingServiceUrl,
            JwtUtils jwtUtils) {

        this.restClient = RestClient.create(parkingServiceUrl);
        this.jwtUtils = jwtUtils;
    }

    public void reserveSpot(String spotId) {

        try {
            patchSpot(spotId, "reserve");

        } catch (RestClientResponseException e) {

            if (e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Parking spot is unavailable or does not exist"
                );
            }

            throw parkingUnavailable(e);

        } catch (RestClientException e) {
            throw parkingUnavailable(e);
        }
    }

    // The reservation is already closed by now, so a failure here is logged rather than thrown
    public void freeSpot(String spotId) {

        try {
            patchSpot(spotId, "cancel-reservation");

        } catch (RestClientResponseException e) {

            // A 409 means the spot is no longer reserved, for example the driver has parked
            if (!e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)) {
                logger.error("Could not free spot {}: {}", spotId, e.getStatusCode());
            }

        } catch (RestClientException e) {
            logger.error("Could not free spot {}: {}", spotId, e.getMessage());
        }
    }

    private void patchSpot(String spotId, String action) {
        restClient.patch()
                .uri("/spots/{spotId}/{action}", spotId, action)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtils.generateServiceToken())
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseStatusException parkingUnavailable(RestClientException e) {
        logger.error("parking-service call failed: {}", e.getMessage());

        return new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Parking service is unavailable"
        );
    }
}
